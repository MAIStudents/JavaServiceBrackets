package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {

	private static Map<String, String> getBracketsMap(String brackets) {
		Map<String, String> result = new TreeMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;

		try {
			jsonNode = objectMapper.readTree(brackets);
		} catch (JsonProcessingException exception) {
			throw new IllegalArgumentException(exception.getMessage());
		}

		JsonNode array = jsonNode.get("bracket");
		if (!array.isEmpty() && array.isArray()) {
			for (JsonNode item : array) {
				result.put(item.get("left").asText(), item.get("right").asText());
			}
		}

		return result;
	}

	@Getter
	public static class BracketAndIndex {
		private final String bracket;
		private final int index;

		BracketAndIndex(String bracket, int index) {
			this.bracket = bracket;
			this.index = index;
		}

	}

	private int processBracket(
					Character bracket, int pos,
					Map<String, String> brackets,
					Deque<BracketAndIndex> stack) {
		String ch = bracket.toString();
		// Left bracket
		if (brackets.containsKey(ch) && !brackets.containsValue(ch)) {
			stack.push(new BracketAndIndex(ch, pos + 1));
			return -1;
		}

		// Right bracket
		if (brackets.containsValue(ch) && !brackets.containsKey(ch)) {
			if (stack.isEmpty() || !ch.equals(brackets.get(stack.peek().getBracket()))) {
				return pos + 1;
			} else {
				stack.pop();
			}
			return -1;
		}

		// May be left or right
		if (brackets.containsValue(ch) && brackets.containsKey(ch)) {
			if (stack.isEmpty() || !ch.equals(brackets.get(stack.peek().getBracket()))) {
				stack.push(new BracketAndIndex(ch, pos + 1));
			} else {
				stack.pop();
			}
		}

		return -1;
	}

	private static class StackPostprocessor {

		public static void stackPostprocess(
						Deque<BracketAndIndex> stack,
						Map<String, String> brackets,
						List<Number> result) {
			while (!stack.isEmpty()) {
				int pos = postprocessIteration(stack, brackets);
				if (pos != -1) {
					result.add(pos);
				}
			}
		}

		private static int postprocessIteration(
						Deque<BracketAndIndex> stack,
						Map<String, String> brackets
		) {
			BracketAndIndex bracketAndIndex = stack.peek();
			stack.pop();
			if (brackets.containsKey(bracketAndIndex.getBracket()) && brackets.containsValue(bracketAndIndex.getBracket())) {
				BracketAndIndex cur = isInStack(stack, bracketAndIndex.getBracket());
				if (cur != null) {
					stack.remove(cur);
					return -1;
				}
			}
			return bracketAndIndex.getIndex();
		}

		private static BracketAndIndex isInStack(Deque<BracketAndIndex> stack, String bracket) {
			List<BracketAndIndex> stackAsList = stack.stream().toList();
			for (int i = stack.size() - 1; i >= 0; --i) {
				if (stackAsList.get(i).getBracket().equals(bracket)) {
					return stackAsList.get(i);
				}
			}
			return null;
		}
	}

	public List<Number> processLine(String line, Map<String, String> brackets) {
		Deque<BracketAndIndex> stack = new ArrayDeque<>();
		List<Number> result = new ArrayList<>();
		for (int i = 0; i < line.length(); ++i) {
			char currentChar = line.charAt(i);

			int errPos = processBracket(currentChar, i, brackets, stack);
			if (errPos != -1) {
				result.add(errPos);
			}
		}

		StackPostprocessor.stackPostprocess(stack, brackets, result);

		return result;
	}

	/**
	 * Checks the content of the file for bracket errors based on the provided configuration.
	 *
	 * @param config  the JSON configuration string defining the bracket pairs
	 * @param content the list of strings representing the content of the file line by line
	 * @return a list of ErrorLocationPoint objects indicating the positions of bracket errors
	 */
	@Override
	public List<ErrorLocationPoint> check(String config, List<String> content) {
		Map<String, String> brackets;
		try {
			brackets = getBracketsMap(config);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return new ArrayList<>();
		}

		List<ErrorLocationPoint> fileErrors = new ArrayList<>();
		for (int index = 0; index < content.size(); ++index) {
			String line = content.get(index);
			int lineNumber = index + 1;

			List<Number> lineErrors = processLine(line, brackets);
			for (Number errIndex : lineErrors) {
				fileErrors.add(new ErrorLocationPoint(lineNumber, (Integer) errIndex));
			}

		}
		return fileErrors;

	}

}