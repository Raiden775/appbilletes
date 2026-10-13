# appbilletes

Proyecto para detectar billetes en euros desde una foto tomada con Android, sin conexión a internet, usando un modelo YOLOv8 exportado a TFLite y ejecutado localmente en el dispositivo.

Objetivo:
- contar billetes por denominación
- sumar el total en euros
- guardar fotos para ampliar el dataset
- preparar un pipeline desde entrenamiento hasta inferencia en Android

Estructura:
- `training/`: scripts para entrenar YOLOv8 y exportarlo a TFLite
- `android_app/`: proyecto Android (Kotlin + CameraX + TensorFlow Lite)

Tecnologías principales:
- Python + Ultralytics YOLOv8
- TensorFlow Lite
- Android Kotlin
- CameraX

## Etapas
1. Recolectar imágenes del montón de billetes con la app Android.
2. Exportar el dataset y etiquetar en formato YOLO.
3. Entrenar el modelo YOLOv8.
4. Exportar a TFLite y copiar el artefacto a la app.
5. Ejecutar inferencia local en Android para contar y sumar euros.

## Rutas clave
- `training/README_training.md`
- `android_app/README_android.md`

## Subir fotos para ampliar el dataset
La app Android guarda fotos en:
- `/Android/data/com.example.eurocounter/files/Pictures/euro_dataset`

## Clases previstas
- `EUR_5`
- `EUR_10`
- `EUR_20`
- `EUR_50`
- `EUR_100`
- `EUR_200`
- `EUR_500`

## Recomendación de MVP
- Un primer modelo con bounding boxes suele funcionar bien si se entrenan con muchas imágenes de billetes apilados y variaciones de iluminación.
- Si hay muchos solapamientos, se puede pasar luego a YOLOv8-seg (segmentación por instancias) para mejorar precisión.

## Siguiente paso
- Estudia `training/README_training.md` para entrenar modelo.
- Estudia `android_app/README_android.md` para correr la app en Android.
