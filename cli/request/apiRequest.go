package request

import (
	"io/ioutil"
	"net/http"
	"bytes"
	"io"
	"errors"
)

type SharedRequest struct {
	server string
	token  string
}

type PostRequest struct {
	sharedRequest SharedRequest
	path          string
	body          string
	contentType   string
}

func MakePostRequestWithoutBody(server string, token string, path string) PostRequest {
	return PostRequest{sharedRequest: SharedRequest{server, token}, path: path}
}

func (request PostRequest) MakePostRequest() (string, error) {
	if (request.sharedRequest.server == "") {
		return "", errors.New("Server must be specified")
	}

	var headers map[string]string

	if (request.sharedRequest.token != "") {
		headers["X-AUTH-TOKEN"] = request.sharedRequest.token
	}

	if (request.body != "") {
		headers["Content-Type"] = "application/json"
	}

	return makeRequest("POST", request.sharedRequest.server + "/" + request.path, headers, request.body)
}

func makeRequest(method string, uri string, headers map[string]string, body string) (string, error) {
	// Create client
	client := &http.Client{}

	var bodyReader io.Reader = nil
	if (body != "") {
		bodyReader = bytes.NewBuffer( []byte(body))
	}

	// Create request
	req, err := http.NewRequest(method, uri, bodyReader)

	// Headers
	for k, v := range headers {
		req.Header.Add(k, v)
	}

	// Fetch Request
	resp, err := client.Do(req)

	if err != nil {
		return "", err
	}

	// Read Response Body
	respBody, _ := ioutil.ReadAll(resp.Body)
	respBodyString := string(respBody)

	if resp.StatusCode >= 400 {
		return "", errors.New("Server returned with error: " + resp.Status + ".\n" + respBodyString)
	}

	return respBodyString, nil
}