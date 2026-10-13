from ultralytics import YOLO
import sys
import os

ckpt = 'runs/train/euros_yolov8n/weights/best.pt'
if not os.path.exists(ckpt):
    print(f'No se encuentra el checkpoint en {ckpt}. Ajusta la ruta.')
    sys.exit(1)

model = YOLO(ckpt)
print('Exportando a TFLite...')
model.export(format='tflite')
print('Export completado. Busca model.tflite en la carpeta del proyecto.')
