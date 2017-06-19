package request

import "os"

type scmCmd struct {
	name string
	cmd  string // name of binary to invoke command

	headLookupCmd    []string
	historyLookupCmd []string
	timeLookupCmd    []string
}

var scmGit = &scmCmd{
	name: "Git",
	cmd:  "git",

	headLookupCmd:    []string{"rev-list --format=%B --max-count=1 {id}"},
	historyLookupCmd: []string{"rev-list -n {depth} {id}"},
	timeLookupCmd:    []string{"git log -1 -s --format=%ci {id}"},
}

func BuildScm(dir string) (*scmCmd, error) {
	_, er := os.Stat(dir + "/" + ".git")
	if os.IsNotExist(er) {
		return nil, er
	}

	return scmGit, nil
}

func