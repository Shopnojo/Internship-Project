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