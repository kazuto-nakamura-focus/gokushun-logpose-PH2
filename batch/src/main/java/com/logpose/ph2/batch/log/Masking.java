package com.logpose.ph2.batch.log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Masking {
	// =====================================================================
	// クラスメンバー
	// =====================================================================
	private Pattern multilinePattern;

	// =====================================================================
	// 公開関数群
	// =====================================================================
	// -------------------------------------------------------------------- 
	/**
	 * マッチングパターンの生成を行う
	 * @param maskPattern マスキングパターンのリスト
	 */
	// -------------------------------------------------------------------- 	
	public void setMaskPattern(List<String> maskPatterns) {
		multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
	}

	// -------------------------------------------------------------------- 
	/**
	 * メッセージのマスキングを行う
	 * @param message マスキング対象のメッセージ
	 */
	// -------------------------------------------------------------------- 	
	public String maskMessage(String message) {
		if (multilinePattern == null) {
			return message;
		}
		StringBuilder sb = new StringBuilder(message);
		Matcher matcher = multilinePattern.matcher(sb);
		while (matcher.find()) {
			IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
				if (matcher.group(group) != null) {
					IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
				}
			});
		}
		return sb.toString();
	}
}
