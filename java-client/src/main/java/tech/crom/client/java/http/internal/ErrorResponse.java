package tech.crom.client.java.http.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
  @JsonProperty(value = "status", required = true)
  private final Integer status;

  @JsonProperty(value = "timestamp", required = true)
  private final Long timestamp;

  @JsonProperty(value = "errorCode", required = true)
  private final String errorCode;

  @JsonProperty(value = "message", required = true)
  private final String message;

  @JsonCreator
  public ErrorResponse(Integer status, Long timestamp, String errorCode, String message) {
    this.status = status;
    this.timestamp = timestamp;
    this.errorCode = errorCode;
    this.message = message;
  }

  public Integer getStatus() {
    return status;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}
