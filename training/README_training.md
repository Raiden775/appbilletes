# Entrenamiento y exportación

## 1) Crear entorno
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
# Windows: venv\Scripts\activate
python -m pip install -r requirements.txt
```

## 2) Preparar dataset en formato YOLO
Estructura recomendada:
```text
dataset/
  images/
    train/
    val/
  labels/
    train/
    val/
```

Cada archivo `.txt` en `labels` debe seguir formato YOLO:
```text
class_id x_center y_center width height
```
Donde los valores son normalizados entre 0 y 1.

## 3) Ajustar `data.yaml`
Asegúrate de que rutas apunten a tu dataset real.

## 4) Entrenar
```bash
python train_yolov8.py
```

## 5) Exportar a TFLite
```bash
python export_tflite.py
```

## 6) Quantization opcional
Si quieres modelo más ligero y rápido, exporta a ONNX y luego convierte con TFLiteConverter usando representative dataset.

## 7) Copiar modelo a Android
Copia `model.tflite` a:
```text
android_app/app/src/main/assets/model.tflite
```

## 8) Probar en Android
Abre el proyecto en Android Studio y ejecuta la app.
