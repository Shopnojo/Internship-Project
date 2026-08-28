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

@app.post("/payments")
def make_payment(payment: PaymentRequest):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    # Check that the due exists and is currently unpaid
    cursor.execute("""
        SELECT
            id,
            student_id,
            net_amount
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
        "amount": due["net_amount"],
        "payment_date": str(today),
        "payment_mode": payment.payment_mode
    }

@app.get("/")
def root():
    return {"message": "ClassAI backend is running"}


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
            penalty,
            waiver
        FROM payment_entries
        WHERE student_id = %s
          AND payment_date IS NULL
        ORDER BY due_date
    """, (student_id,))

    dues = cursor.fetchall()

    cursor.close()
    connection.close()

    return dues