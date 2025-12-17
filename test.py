# test_cam.py
import cv2

cap = cv2.VideoCapture(0)

if not cap.isOpened():
    print("❌ Không mở được webcam")
else:
    print("✅ Webcam đã mở")
    while True:
        ret, frame = cap.read()
        if not ret:
            print("❌ Không đọc được frame")
            break

        cv2.imshow("Webcam", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()
