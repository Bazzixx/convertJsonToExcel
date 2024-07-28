package com.example.jsontoexcel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class convert {
	public static void main(String[] args) {
		// if (args.length != 2) {
		// 	System.out.println("Usage: java JsonToCsvConverter <input_directory> <output_directory>");
		// 	return;
		// }
		//
		// String inputDirectoryPath = args[0];
		// String outputDirectoryPath = args[1];

		File inputDirectory = new File("/Users/bazzi/Documents/json");
		File outputDirectory = new File("/Users/bazzi/Documents/csv");

		if (!inputDirectory.isDirectory()) {
			System.out.println("Input directory does not exist.");
			return;
		}

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		File[] jsonFiles = inputDirectory.listFiles((dir, name) -> name.endsWith(".json"));


		if (jsonFiles == null || jsonFiles.length == 0) {
			System.out.println("No JSON files found in the input directory.");
			return;
		}

		for (File jsonFile : jsonFiles) {
			String csvFileName = jsonFile.getName().replace(".json", ".csv");
			String csvFilePath = new File(outputDirectory, csvFileName).getPath();
			convertJsonToCsv(jsonFile.getPath(), csvFilePath);
			System.out.println(jsonFile.getName() + " converted to " + csvFileName);
		}
	}

	private static void convertJsonToCsv(String jsonFilePath, String csvFilePath) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFilePath));



			// CSV 파일 쓰기 (UTF-8로 인코딩)
			try (Writer outputStreamWriter = new FileWriter(csvFilePath);
				 BufferedWriter csvWriter = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(csvFilePath), StandardCharsets.UTF_8))) {

				// BOM 쓰기
				csvWriter.write("\uFEFF");

				// CSV 헤더 작성
				csvWriter.write("ID,Category,documentId,Title,Topic,Relation,form");
				csvWriter.newLine();

				// id, metadata, document에서 데이터 추출하여 CSV에 쓰기
				String id = (String) jsonObject.get("id");
				JSONObject metadata = (JSONObject) jsonObject.get("metadata");
				String category = (String) metadata.get("category");

				JSONArray document = (JSONArray) jsonObject.get("document");
				for (Object doc : document) {
					JSONObject documentObj = (JSONObject) doc;
					String documentId = (String) documentObj.get("id");
					JSONObject documentMetadata = (JSONObject) documentObj.get("metadata");
					String title = (String) documentMetadata.get("title");
					String topic = (String) documentMetadata.get("topic");
					JSONObject setting = (JSONObject) documentMetadata.get("setting");
					String relation = (String) setting.get("relation");

					JSONArray utterance = (JSONArray) documentObj.get("utterance");
					for (Object utter : utterance) {
						JSONObject utteranceObj = (JSONObject) utter;
						String form = (String) utteranceObj.get("form");

						// CSV에 쓰기
						csvWriter.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", id, category, documentId, title, topic, relation, form));
						csvWriter.newLine();
					}
				}

				System.out.println("JSON 파일이 성공적으로 CSV로 변환되었습니다.");
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
