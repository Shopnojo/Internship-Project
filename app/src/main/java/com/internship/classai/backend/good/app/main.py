import hashlib
import secrets

from fastapi import FastAPI
from database import get_db_connection

from datetime import date
from pydantic import BaseModel
from typing import Optional

app = FastAPI()


class PaymentRequest(BaseModel):
    due_id: int
    payment_mode: str
    transaction_no: Optional[str] = None
    remarks: Optional[str] = None


class EmployeeCreate(BaseModel):
    schoolId: int
    fullName: str
    mobile: str
    userId: str
    password: str

class AdminCreate(BaseModel):
    fullName: str
    mobile: str
    userId: str
    password: str

class LoginRequest(BaseModel):
    userId: str
    password: str
    role: str

@app.post("/payments")
def make_payment(payment: PaymentRequest):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    # Get the unpaid due and all authoritative financial/payment values
    cursor.execute("""
        SELECT
            id,
            student_id,
            amount,
            COALESCE(penalty, 0) AS penalty,
            COALESCE(waiver, 0) AS waiver,
            net_amount,
            transaction_no
        FROM payment_entries
        WHERE id = %s
          AND is_paid = 0
          AND payment_date IS NULL
    """, (payment.due_id,))

    due = cursor.fetchone()

    if not due:
        cursor.close()
        connection.close()

        return {
            "success": False,
            "message": "Due not found or already paid"
        }

    today = date.today()

    # Mark the due as paid
    cursor.execute("""
        UPDATE payment_entries
        SET
            payment_date = %s,
            payment_confirm_date = %s,
            payment_recieve_date = %s,
            payment_mode = %s,
            transaction_no = %s,
            remarks = %s,
            is_paid = 1,
            receipt_date = %s,
            updated_ts = CURRENT_TIMESTAMP
        WHERE id = %s
    """, (
        today,
        today,
        today,
        payment.payment_mode,
        payment.transaction_no,
        payment.remarks,
        today,
        payment.due_id
    ))

    connection.commit()

    cursor.close()
    connection.close()

    return {
        "success": True,
        "message": "Payment recorded successfully",
        "due_id": payment.due_id,
        "student_id": due["student_id"],

        # Exact transaction number supplied by Android
        "transaction_no": payment.transaction_no,

        # Values directly from payment_entries
        # NULL penalty/waiver are normalized to 0
        "amount": due["amount"],
        "penalty": due["penalty"],
        "waiver": due["waiver"],
        "net_amount": due["net_amount"],

        "payment_date": str(today),
        "payment_mode": payment.payment_mode
    }


@app.get("/")
def root():
    return {
        "message": "ClassAI backend is running"
    }


@app.get("/test-db")
def test_database():

    connection = get_db_connection()
    cursor = connection.cursor()

    cursor.execute("SELECT 1")
    result = cursor.fetchone()

    cursor.close()
    connection.close()

    return {
        "database": "connected",
        "result": result[0]
    }


@app.get("/students")
def get_students():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    cursor.execute("""
        SELECT
            id,
            student_id AS studentId,
            CONCAT(first_name, ' ', last_name) AS name,
            class_id AS classId,
            section_id AS sectionId
        FROM student_master
        WHERE school_id = 3
          AND is_active = 1
    """)

    students = cursor.fetchall()

    cursor.close()
    connection.close()

    return students


@app.get("/classes")
def get_classes():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    cursor.execute("""
        SELECT
            id,
            class AS name
        FROM class_master
        WHERE school_id = 3
          AND is_active = 1
        ORDER BY id
    """)

    classes = cursor.fetchall()

    cursor.close()
    connection.close()

    return classes


@app.get("/sections")
def get_sections():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    cursor.execute("""
        SELECT
            id,
            class_id AS classId,
            section AS name
        FROM section_master
        WHERE school_id = 3
          AND is_active = 1
        ORDER BY class_id, id
    """)

    sections = cursor.fetchall()

    cursor.close()
    connection.close()

    return sections


@app.get("/dues/{student_id}")
def get_student_dues(student_id: int):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    cursor.execute("""
        SELECT
            id,
            student_id AS studentId,
            DATE_FORMAT(due_date, '%M-%Y') AS month,
            DATE_FORMAT(due_date, '%Y-%m-%d') AS dueDate,
            amount AS payableAmount,
            COALESCE(penalty, 0) AS penalty,
            COALESCE(waiver, 0) AS waiver,
            net_amount AS netAmount
        FROM payment_entries
        WHERE student_id = %s
          AND payment_date IS NULL
        ORDER BY due_date
    """, (student_id,))

    dues = cursor.fetchall()

    cursor.close()
    connection.close()

    return dues


# ---------------------------------------------------------------------------
# School / Employee management
# ---------------------------------------------------------------------------

@app.get("/schools")
def get_schools():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                id,
                school_name AS schoolName,
                is_active AS isActive
            FROM school_master
            ORDER BY school_name, id
        """)

        return cursor.fetchall()

    finally:
        cursor.close()
        connection.close()


@app.post("/employees")
def create_employee(employee: EmployeeCreate):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        # Make sure the selected school exists and is active.
        cursor.execute("""
            SELECT
                id,
                is_active
            FROM school_master
            WHERE id = %s
        """, (employee.schoolId,))

        school = cursor.fetchone()

        if not school:
            return {
                "success": False,
                "message": "School not found"
            }

        if int(school["is_active"]) != 1:
            return {
                "success": False,
                "message": "Selected school is inactive"
            }

        # User ID must be unique among employees.
        cursor.execute("""
            SELECT id
            FROM user_accountant
            WHERE user_id = %s
            LIMIT 1
        """, (employee.userId,))

        existing_employee = cursor.fetchone()

        if existing_employee:
            return {
                "success": False,
                "message": "Employee User ID already exists"
            }

        # Generate a random salt and hash the password.
        salt = secrets.token_bytes(16)

        password_hash = hashlib.pbkdf2_hmac(
            "sha256",
            employee.password.encode("utf-8"),
            salt,
            100000
        )

        stored_password = (
            "pbkdf2_sha256$100000$"
            + salt.hex()
            + "$"
            + password_hash.hex()
        )

        cursor.execute("""
            INSERT INTO user_accountant (
                school_id,
                full_name,
                mobile,
                user_id,
                password,
                is_active,
                created_ts
            )
            VALUES (
                %s,
                %s,
                %s,
                %s,
                %s,
                1,
                CURRENT_TIMESTAMP
            )
        """, (
            employee.schoolId,
            employee.fullName,
            employee.mobile,
            employee.userId,
            stored_password
        ))

        connection.commit()

        return {
            "success": True,
            "message": "Employee created successfully",
            "employeeId": cursor.lastrowid
        }

    except Exception as e:
        connection.rollback()

        return {
            "success": False,
            "message": str(e)
        }

    finally:
        cursor.close()
        connection.close()


@app.get("/employees")
def get_employees():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                ua.id,
                ua.full_name AS fullName,
                ua.mobile,
                ua.user_id AS userId,
                ua.school_id AS schoolId,
                sm.school_name AS schoolName,
                ua.is_active AS isActive
            FROM user_accountant ua
            LEFT JOIN school_master sm
                ON sm.id = ua.school_id
            ORDER BY ua.full_name, ua.id
        """)

        return cursor.fetchall()

    finally:
        cursor.close()
        connection.close()

@app.post("/admins")
def create_admin(admin: AdminCreate):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        # User ID must be unique across both admin and employee accounts.
        cursor.execute("""
            SELECT user_id
            FROM user_admin
            WHERE user_id = %s

            UNION ALL

            SELECT user_id
            FROM user_accountant
            WHERE user_id = %s

            LIMIT 1
        """, (admin.userId, admin.userId))

        existing_user = cursor.fetchone()

        if existing_user:
            return {
                "success": False,
                "message": "User ID already exists"
            }

        # Generate a random salt and hash the password.
        salt = secrets.token_bytes(16)

        password_hash = hashlib.pbkdf2_hmac(
            "sha256",
            admin.password.encode("utf-8"),
            salt,
            100000
        )

        stored_password = (
            "pbkdf2_sha256$100000$"
            + salt.hex()
            + "$"
            + password_hash.hex()
        )

        cursor.execute("""
            INSERT INTO user_admin (
                full_name,
                mobile,
                user_id,
                password,
                is_active,
                created_ts
            )
            VALUES (
                %s,
                %s,
                %s,
                %s,
                1,
                CURRENT_TIMESTAMP
            )
        """, (
            admin.fullName,
            admin.mobile,
            admin.userId,
            stored_password
        ))

        connection.commit()

        return {
            "success": True,
            "message": "Admin created successfully",
            "adminId": cursor.lastrowid
        }

    except Exception as e:
        connection.rollback()

        return {
            "success": False,
            "message": str(e)
        }

    finally:
        cursor.close()
        connection.close()

@app.post("/login")
def login(request: LoginRequest):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        # ---------------------------------------------------------------
        # Find the user in the table matching the selected login role.
        #
        # Admin    -> user_admin
        # Employee -> user_accountant
        #
        # The backend is the authority for the role.
        # ---------------------------------------------------------------
        if request.role == "admin":

            cursor.execute("""
                SELECT
                    id,
                    full_name,
                    user_id,
                    password,
                    is_active
                FROM user_admin
                WHERE user_id = %s
                LIMIT 1
            """, (request.userId,))

        elif request.role == "employee":

            cursor.execute("""
                SELECT
                    ua.id,
                    ua.full_name,
                    ua.user_id,
                    ua.password,
                    ua.is_active,
                    sm.is_active AS school_is_active
                FROM user_accountant ua
                LEFT JOIN school_master sm
                    ON sm.id = ua.school_id
                WHERE ua.user_id = %s
                LIMIT 1
            """, (request.userId,))

        else:
            return {
                "success": False,
                "message": "Invalid login role"
            }

        user = cursor.fetchone()

        if not user:
            return {
                "success": False,
                "message": "Invalid User ID or password"
            }

        # ---------------------------------------------------------------
        # Verify account is active.
        # ---------------------------------------------------------------
        if int(user["is_active"]) != 1:
            return {
                "success": False,
                "message": "Account is inactive"
            }

        # ---------------------------------------------------------------
        # Employee must also belong to an active school.
        # ---------------------------------------------------------------
        if request.role == "employee":

            if user["school_is_active"] is None:
                return {
                    "success": False,
                    "message": "Employee school not found"
                }

            if int(user["school_is_active"]) != 1:
                return {
                    "success": False,
                    "message": "School is inactive"
                }

        # ---------------------------------------------------------------
        # Verify PBKDF2 password.
        #
        # Stored format:
        # pbkdf2_sha256$100000$salt$hash
        # ---------------------------------------------------------------
        stored_password = user["password"]

        try:
            algorithm, iterations, salt_hex, stored_hash_hex = (
                stored_password.split("$")
            )

            if algorithm != "pbkdf2_sha256":
                return {
                    "success": False,
                    "message": "Invalid account credentials"
                }

            iterations = int(iterations)

            salt = bytes.fromhex(salt_hex)

            calculated_hash = hashlib.pbkdf2_hmac(
                "sha256",
                request.password.encode("utf-8"),
                salt,
                iterations
            )

            if calculated_hash.hex() != stored_hash_hex:
                return {
                    "success": False,
                    "message": "Invalid User ID or password"
                }

        except Exception:
            return {
                "success": False,
                "message": "Invalid account credentials"
            }

        # ---------------------------------------------------------------
        # Successful authentication
        # ---------------------------------------------------------------
        return {
            "success": True,
            "message": "Login successful",
            "userId": user["user_id"],
            "role": request.role,
            "fullName": user["full_name"]
        }

    finally:
        cursor.close()
        connection.close()