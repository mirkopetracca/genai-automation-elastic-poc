package com.demo.ai.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class EmbeddingUtils {

	public static double cosineSimilarity(byte[] vec1, byte[] vec2) {

		DoubleBuffer buffer1 = ByteBuffer.wrap(vec1).asDoubleBuffer();
		DoubleBuffer buffer2 = ByteBuffer.wrap(vec2).asDoubleBuffer();

		double dotProduct = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;

		while (buffer1.hasRemaining() && buffer2.hasRemaining()) {

			double v1 = buffer1.get();
			double v2 = buffer2.get();
			dotProduct += v1 * v2;
			norm1 += v1 * v1;
			norm2 += v2 * v2;

		}

		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));

	}

	public static double cosineSimilarity(List<Float> vec1, List<Float> vec2) {

		if (vec1.size() != vec2.size()) {

			throw new IllegalArgumentException("I vettori devono avere la stessa lunghezza.");

		}

		double dotProduct = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;

		for (int i = 0; i < vec1.size(); i++) {

			float v1 = vec1.get(i);
			float v2 = vec2.get(i);
			dotProduct += v1 * v2;
			norm1 += v1 * v1;
			norm2 += v2 * v2;

		}

		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));

	}

	public static List<Float> convertByteArrayToFloatList(byte[] byteArray) {

		if (byteArray.length % 4 != 0) {

			throw new IllegalArgumentException("Il byte[] non è un multiplo di 4, impossibile convertirlo in float[].");

		}

		ByteBuffer buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
		float[] floatArray = new float[byteArray.length / 4];

		for (int i = 0; i < floatArray.length; i++) {

			floatArray[i] = buffer.getFloat();

		}

		List<Float> floatList = new ArrayList<>();

		for (float value : floatArray) {

			floatList.add(value);

		}

		return floatList;

	}

	public static float[] convertByteArrayToFloatArray(byte[] byteArray) {

		if (byteArray.length % 4 != 0) {

			throw new IllegalArgumentException("Il byte[] non è un multiplo di 4, impossibile convertirlo in float[].");

		}

		ByteBuffer buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
		float[] floatArray = new float[byteArray.length / 4];

		for (int i = 0; i < floatArray.length; i++) {

			floatArray[i] = buffer.getFloat();

		}

		return floatArray;

	}

	public static List<Float> convertFloatArrayToList(float[] floatArray) {

		List<Float> floatList = new ArrayList<>();

		for (float value : floatArray) {

			floatList.add(value);

		}

		return floatList;

	}
	
	public static float[] convertFloatListToArray(List<Float> floatList) {

		return ArrayUtils.toPrimitive(floatList.toArray(new Float[0]), 0.0F);

	}
	
}