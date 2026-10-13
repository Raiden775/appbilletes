# Proyecto Android para appbilletes

## Qué hace
- Toma fotos con la cámara del móvil.
- Guarda las imágenes para ampliar el dataset de billetes.
- Exporta un ZIP del dataset para etiquetar en el PC.
- Ejecuta inferencia local con TFLite (cuando el modelo está listo).
- Muestra el número de billetes por denominación y la suma total en euros.

## Requisitos
- Android 9+ recomendado
- Android Studio
- Un teléfono físico o emulador con cámara

## Pasos
1. Abre este proyecto en Android Studio.
2. Copia tu `model.tflite` en:
   ```text
   android_app/app/src/main/assets/model.tflite
   ```
3. Ejecuta la app en el dispositivo.
4. Pulsa "Tomar foto" para guardar fotos en el dataset.
5. Usa "Exportar dataset" para compartir el ZIP y etiquetar imágenes.

## Notas
- La inferencia local requiere un modelo TFLite compatible con `ObjectDetector`.
- Si el modelo exportado no cumple la firma deseada, se puede adaptar el parser o usar un export custom.

## Clases esperadas
- `EUR_5`
- `EUR_10`
- `EUR_20`
- `EUR_50`
- `EUR_100`
- `EUR_200`
- `EUR_500`
