package fr.gquilici.cql.json;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.OperandsParser;

public class JsonOperandsParser implements OperandsParser<JsonNode> {

	@Override
	public List<String> parseAsString(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asText).toList();
	}

	@Override
	public List<Boolean> parseAsBoolean(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asBoolean).toList();
	}

	@Override
	public List<Integer> parseAsInteger(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asInt).toList();
	}

	@Override
	public List<Long> parseAsLong(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asLong).toList();
	}

	@Override
	public List<Float> getAsFloat(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asDouble).map(Double::floatValue).toList();
	}

	@Override
	public List<Double> parseAsDouble(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asDouble).toList();
	}

	@Override
	public List<LocalDate> parseAsLocalDate(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asText).map(LocalDate::parse).toList();
	}

	@Override
	public List<Instant> parseAsInstant(List<JsonNode> operands) {
		return operands.stream().map(JsonNode::asText).map(Instant::parse).toList();
	}

}
