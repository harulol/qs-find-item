package com.github.pocketkid2.finditem;

import java.util.List;
import java.util.stream.Collectors;

public class IntSegment {

	private int min;
	private int max;

	public IntSegment(int a, int b) {
		if (b < a) {
			min = b;
			max = a;
		} else {
			min = a;
			max = b;
		}
	}

	public IntSegment(String s) {
		this(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]));
	}

	public boolean contains(int c) {
		return c <= max && c >= min;
	}

	public Integer size() {
		return max - min + 1;
	}

	public static int listSum(List<IntSegment> segments) {
		return (int) segments.stream().collect(Collectors.summarizingInt(IntSegment::size)).getSum();
	}
}
