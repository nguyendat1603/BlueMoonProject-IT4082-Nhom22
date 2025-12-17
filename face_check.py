import cv2
import face_recognition
import sys
import os

def capture_image():
    cam = cv2.VideoCapture(0)
    if not cam.isOpened():
        print("false")
        return None

    print("Press 'q' to capture image.")
    while True:
        ret, frame = cam.read()
        if not ret:
            print("false")
            return None
        cv2.imshow("Capture Face", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            cv2.imwrite("webcam_frame.jpg", frame)
            break

    cam.release()
    cv2.destroyAllWindows()
    return "webcam_frame.jpg"

def check_face(email):
    # Đường dẫn ảnh lưu sẵn theo email
    saved_image_path = f"{email}.jpg"
    if not os.path.exists(saved_image_path):
        print("false")
        return

    captured_image_path = capture_image()
    if not captured_image_path:
        print("false")
        return

    try:
        # Load ảnh
        saved_image = face_recognition.load_image_file(saved_image_path)
        captured_image = face_recognition.load_image_file(captured_image_path)

        # Mã hóa khuôn mặt
        saved_encoding = face_recognition.face_encodings(saved_image) #chuyển sang vector encoding 128 chiều (128*128)
        captured_encoding = face_recognition.face_encodings(captured_image)

        if not saved_encoding or not captured_encoding:
            print("false")
            return

        # So sánh
        result = face_recognition.compare_faces([saved_encoding[0]], captured_encoding[0])
        #face_recognition.compare_faces(known_face_encodings, face_encoding_to_check, tolerance=0.6) nếu khoảng cách nhỏ hơn 0.6 thì thoả mãn

        print("true" if result[0] else "false")

    except Exception as e:
        print("false")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("false")
    else:
        email = sys.argv[1]
        check_face(email)
