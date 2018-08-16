import numpy as np
import tensorflow as tf

from keras.preprocessing import image
from keras.applications.vgg16 import VGG16, preprocess_input
from keras.models import Model

class FeatureExtractor:
    def __init__(self):
        # 이미지 넷으로 사전 학습된 모델을 가져옵니다.
        base_model = VGG16(weights='imagenet')
        self.model = Model(inputs=base_model.input, outputs=base_model.get_layer('fc1').output)
        self.graph = tf.get_default_graph()

    def extract(self, img):
        # VGG16 알고리즘에 맞게 이미지 크기를 224 x 224로 설정합니다.
        img = img.resize((224, 224))  

        # 이미지가 색상 정보를 가지도록 설정합니다.
        img = img.convert('RGB')  

        # Numpy 배열 객체로 변환합니다.
        x = image.img_to_array(img)  

        # 이미지의 번호가 들어갈 수 있도록 원소를 하나 확장합니다.
        x = np.expand_dims(x, axis=0)  

        # 전처리를 수행합니다.
        x = preprocess_input(x)  

        with self.graph.as_default():
            # 기존의 모델을 기반으로 특징을 추출합니다.
            feature = self.model.predict(x)[0]  
            
            # 정규화를 수행합니다.
            return feature / np.linalg.norm(feature)  