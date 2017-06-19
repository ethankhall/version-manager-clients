package request

import (
	"encoding/json"
	"os"
	"gopkg.in/src-d/go-git.v4"
)

type ServiceDetails struct {
	server string
	token  string
}

type RepositoryDetails struct {
	projectName string
	repoName    string
}

func CreateProject(name string, details ServiceDetails) bool {
	err, _ := PostRequest{sharedRequest: details, path: "/api/v1/project/" + name}.MakePostRequest()
	return err == ""
}

func CreateRepository(repoDetails RepositoryDetails, serviceDetails ServiceDetails, bumper string, scmUrl string, description string) bool {

	jsonBody := struct {
		Bumper      string    `json:"bumper"`
		ScmUrl      string    `json:"scmUrl"`
		Description string    `json:"description"`
	}{bumper, scmUrl, description}

	marshaledJson, _ := json.Marshal(jsonBody)

	err, _ := PostRequest{sharedRequest: serviceDetails, path: repoPath(repoDetails), body: string(marshaledJson)}.MakePostRequest()
	return err == ""
}

func CreateVersion(repoDetails RepositoryDetails, serviceDetails ServiceDetails, repo os.File) string {
	git.
	return ""
}

func repoPath(details RepositoryDetails) string {
	return "/api/v1/project/" + details.projectName + "/repo/" + details.repoName
}
