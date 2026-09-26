import hashlib
import secrets
from urllib.parse import urlencode
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError
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


class AdminStatusUpdate(BaseModel):
    isActive: int


class EmployeeUpdate(BaseModel):
    schoolId: int
    fullName: str
    mobile: str
    userId: str
    password: Optional[str] = None


class LoginRequest(BaseModel):
    userId: str
    password: str
    role: str


@app.post("/payments")
def make_payment(payment: PaymentRequest):
    """Legacy backend payment endpoint kept for compatibility.

    The Android app now confirms the payment through the parent website's
    existing /submit-payment endpoint first. This endpoint remains available
    for existing callers and preserves the original ClassAI DB payment flow.
    """

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                id,
                student_id,
                amount,
                COALESCE(penalty, 0) AS penalty,
                COALESCE(waiver, 0) AS waiver,
                net_amount
            FROM payment_entries
            WHERE id = %s
              AND is_paid = 0
              AND payment_date IS NULL
            LIMIT 1
        """, (payment.due_id,))

        due = cursor.fetchone()

        if not due:
            return {
                "success": False,
                "message": "Due not found or already paid"
            }

        today = date.today()

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

        # Keep the existing ClassAI payment flow intact. After the payment is
        # already committed, ask the unchanged parent website to run its own
        # receipt-generation flow. A failure here must never make a successful
        # ClassAI payment fail.
        try:
            form_data = urlencode({
                "payment_id": str(payment.due_id),
                "payment_date": today.isoformat(),
                "payment_mode": payment.payment_mode,
                "remarks": payment.remarks or "",
            }).encode("utf-8")

            website_request = Request(
                "https://classai.fixmyneed.in/submit-payment",
                data=form_data,
                method="POST",
                headers={
                    "Content-Type": "application/x-www-form-urlencoded",
                    "User-Agent": "ClassAI-Backend/1.0",
                },
            )

            try:
                with urlopen(website_request, timeout=20) as website_response:
                    website_status = website_response.status
            except HTTPError as exc:
                website_status = exc.code

            if not (200 <= website_status < 400):
                print(
                    f"Website receipt generation returned HTTP {website_status} "
                    f"for payment {payment.due_id}"
                )

        except (URLError, TimeoutError, OSError) as exc:
            print(
                f"Website receipt generation failed for payment "
                f"{payment.due_id}: {exc}"
            )

        return {
            "success": True,
            "message": "Payment recorded successfully",
            "due_id": payment.due_id,
            "student_id": due["student_id"],
            "transaction_no": payment.transaction_no,
            "amount": due["amount"],
            "penalty": due["penalty"],
            "waiver": due["waiver"],
            "net_amount": due["net_amount"],
            "payment_date": str(today),
            "payment_mode": payment.payment_mode
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


@app.get("/payments/{payment_id}")
def get_confirmed_payment(payment_id: int):
    """Return the payment record after the parent website confirms it."""

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                id,
                student_id,
                amount,
                COALESCE(penalty, 0) AS penalty,
                COALESCE(waiver, 0) AS waiver,
                net_amount,
                payment_date,
                payment_mode,
                transaction_no,
                bill_no,
                receipt_no,
                receipt_date,
                receipt,
                is_paid
            FROM payment_entries
            WHERE id = %s
            LIMIT 1
        """, (payment_id,))

        payment = cursor.fetchone()

        if not payment or int(payment.get("is_paid") or 0) != 1:
            return {
                "success": False,
                "message": "Payment has not been confirmed"
            }

        if not payment.get("receipt"):
            return {
                "success": False,
                "message": "Payment was confirmed but the website receipt is not available yet"
            }

        return {
            "success": True,
            "message": "Payment recorded successfully",
            "due_id": payment["id"],
            "student_id": payment["student_id"],
            "transaction_no": payment.get("transaction_no"),
            "amount": payment["amount"],
            "penalty": payment["penalty"],
            "waiver": payment["waiver"],
            "net_amount": payment["net_amount"],
            "payment_date": str(payment["payment_date"]) if payment.get("payment_date") else None,
            "payment_mode": payment.get("payment_mode"),
            "bill_no": payment.get("bill_no"),
            "receipt_no": payment.get("receipt_no"),
            "receipt_date": str(payment["receipt_date"]) if payment.get("receipt_date") else None,
            "receipt": payment.get("receipt")
        }

    finally:
        cursor.close()
        connection.close()


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

        # User ID must be unique across both employee and admin accounts.
        cursor.execute("""
            SELECT user_id
            FROM user_admin
            WHERE user_id = %s

            UNION ALL

            SELECT user_id
            FROM user_accountant
            WHERE user_id = %s

            LIMIT 1
        """, (employee.userId, employee.userId))

        existing_user = cursor.fetchone()

        if existing_user:
            return {
                "success": False,
                "message": "User ID already exists"
            }

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


@app.patch("/employees/{employee_id}")
def update_employee(
    employee_id: int,
    employee: EmployeeUpdate
):
    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id, user_id, is_active
            FROM user_accountant
            WHERE id = %s
            LIMIT 1
        """, (employee_id,))

        existing_employee = cursor.fetchone()

        if not existing_employee:
            return {
                "success": False,
                "message": "Employee not found"
            }

        cursor.execute("""
            SELECT id, is_active
            FROM school_master
            WHERE id = %s
            LIMIT 1
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

        # User ID uniqueness is global across admins and employees, excluding
        # the employee currently being edited.
        cursor.execute("""
            SELECT user_id
            FROM user_admin
            WHERE user_id = %s

            UNION ALL

            SELECT user_id
            FROM user_accountant
            WHERE user_id = %s
              AND id <> %s

            LIMIT 1
        """, (employee.userId, employee.userId, employee_id))

        existing_user = cursor.fetchone()

        if existing_user:
            return {
                "success": False,
                "message": "User ID already exists"
            }

        if employee.password and employee.password.strip():
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
                UPDATE user_accountant
                SET school_id = %s,
                    full_name = %s,
                    mobile = %s,
                    user_id = %s,
                    password = %s
                WHERE id = %s
            """, (
                employee.schoolId,
                employee.fullName,
                employee.mobile,
                employee.userId,
                stored_password,
                employee_id
            ))
        else:
            cursor.execute("""
                UPDATE user_accountant
                SET school_id = %s,
                    full_name = %s,
                    mobile = %s,
                    user_id = %s
                WHERE id = %s
            """, (
                employee.schoolId,
                employee.fullName,
                employee.mobile,
                employee.userId,
                employee_id
            ))

        connection.commit()

        return {
            "success": True,
            "message": "Employee updated successfully"
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


@app.patch("/employees/{employee_id}/status")
def update_employee_status(
    employee_id: int,
    request: AdminStatusUpdate
):
    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                ua.id,
                ua.is_active,
                sm.is_active AS school_is_active
            FROM user_accountant ua
            LEFT JOIN school_master sm
                ON sm.id = ua.school_id
            WHERE ua.id = %s
            LIMIT 1
        """, (employee_id,))

        employee = cursor.fetchone()

        if not employee:
            return {
                "success": False,
                "message": "Employee not found"
            }

        new_status = 1 if int(request.isActive) == 1 else 0

        if new_status == 1 and (
            employee["school_is_active"] is None
            or int(employee["school_is_active"]) != 1
        ):
            return {
                "success": False,
                "message": "Employee cannot be activated while the school is inactive"
            }

        cursor.execute("""
            UPDATE user_accountant
            SET is_active = %s
            WHERE id = %s
        """, (new_status, employee_id))

        connection.commit()

        return {
            "success": True,
            "message": "Employee status updated successfully",
            "isActive": new_status
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


@app.patch("/schools/{school_id}/status")
def update_school_status(
    school_id: int,
    request: AdminStatusUpdate
):
    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT id, is_active
            FROM school_master
            WHERE id = %s
            LIMIT 1
        """, (school_id,))

        school = cursor.fetchone()

        if not school:
            return {
                "success": False,
                "message": "School not found"
            }

        new_status = 1 if int(request.isActive) == 1 else 0

        cursor.execute("""
            UPDATE school_master
            SET is_active = %s
            WHERE id = %s
        """, (new_status, school_id))

        # School status controls all employees belonging to that school.
        # Deactivation disables every employee; reactivation restores them.
        cursor.execute("""
            UPDATE user_accountant
            SET is_active = %s
            WHERE school_id = %s
        """, (new_status, school_id))

        connection.commit()

        return {
            "success": True,
            "message": "School status updated successfully",
            "isActive": new_status
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



# ---------------------------------------------------------------------------
# Admin management
# ---------------------------------------------------------------------------

@app.post("/admins")
def create_admin(admin: AdminCreate):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        # testadmin is permanently reserved for the SuperAdmin.
        if admin.userId.strip() == "testadmin":
            return {
                "success": False,
                "message": "This User ID is reserved for the SuperAdmin"
            }

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


@app.get("/admins")
def get_admins():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        # testadmin is the protected SuperAdmin and must never
        # appear in the Administrators directory.
        cursor.execute("""
            SELECT
                id,
                full_name AS fullName,
                mobile,
                user_id AS userId,
                is_active AS isActive
            FROM user_admin
            WHERE user_id <> 'testadmin'
            ORDER BY full_name, id
        """)

        return cursor.fetchall()

    finally:
        cursor.close()
        connection.close()


@app.patch("/admins/{admin_id}")
def update_admin_status(
    admin_id: int,
    request: AdminStatusUpdate
):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        cursor.execute("""
            SELECT
                id,
                user_id
            FROM user_admin
            WHERE id = %s
            LIMIT 1
        """, (admin_id,))

        admin = cursor.fetchone()

        if not admin:
            return {
                "success": False,
                "message": "Admin not found"
            }

        # SuperAdmin can never be modified.
        if admin["user_id"] == "testadmin":
            return {
                "success": False,
                "message": "SuperAdmin cannot be modified"
            }

        new_status = 1 if int(request.isActive) == 1 else 0

        cursor.execute("""
            UPDATE user_admin
            SET is_active = %s
            WHERE id = %s
        """, (
            new_status,
            admin_id
        ))

        connection.commit()

        return {
            "success": True,
            "message": "Admin status updated successfully",
            "isActive": new_status
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


@app.get("/profile")
def get_profile(user_id: str, role: str):
    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:
        if role == "admin":
            cursor.execute("""
                SELECT
                    ua.full_name AS fullName,
                    ua.user_id AS userId,
                    ua.mobile,
                    ua.is_active AS isActive
                FROM user_admin ua
                WHERE ua.user_id = %s
                LIMIT 1
            """, (user_id,))

            user = cursor.fetchone()

            if not user:
                return {
                    "success": False,
                    "message": "Profile not found"
                }

            user["role"] = "Administrator"
            user["schoolName"] = ""
            return {
                "success": True,
                **user
            }

        if role == "employee":
            cursor.execute("""
                SELECT
                    ua.full_name AS fullName,
                    ua.user_id AS userId,
                    ua.mobile,
                    ua.is_active AS isActive,
                    sm.school_name AS schoolName,
                    sm.is_active AS schoolIsActive
                FROM user_accountant ua
                LEFT JOIN school_master sm
                    ON sm.id = ua.school_id
                WHERE ua.user_id = %s
                LIMIT 1
            """, (user_id,))

            user = cursor.fetchone()

            if not user:
                return {
                    "success": False,
                    "message": "Profile not found"
                }

            user["role"] = "Employee"
            return {
                "success": True,
                **user
            }

        return {
            "success": False,
            "message": "Invalid profile role"
        }

    finally:
        cursor.close()
        connection.close()


# ---------------------------------------------------------------------------
# Login
# ---------------------------------------------------------------------------

@app.post("/login")
def login(request: LoginRequest):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    try:

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

        if int(user["is_active"]) != 1:

            return {
                "success": False,
                "message": "Account is inactive"
            }

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