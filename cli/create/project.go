// Copyright © 2017 NAME HERE <EMAIL ADDRESS>
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package create

import (
	"errors"
	"github.com/spf13/cobra"
	"github.com/ethankhall/version-manager-clients/cli/request"
	"fmt"
	"os"
	"log"
)
var logger = log.New(os.Stdout, "", 0)


// projectCmd represents the project command
var CreateProjectCmd = &cobra.Command{
	Use:   "project [name]",
	Short: "Creates a Project",
	Long: `Creates a new project. Usually you would only want one project, but there
are times whre two can be useful`,
	PreRunE: func(cmd *cobra.Command, args []string) error {
		if (len(args) != 1) {
			return errors.New("Create project expectes exactly one argument, the project name.")
		}
		return nil
	},
	Run: func(cmd *cobra.Command, args []string) {
		requestWithoutBody := request.MakePostRequestWithoutBody(
			cmd.Flag("api-server").Value.String(),
			cmd.Flag("api-token").Value.String(),
			"api/v1/project/" + args[0])
		_, err := requestWithoutBody.MakePostRequest()

		if(err != nil) {
			logger.Fatal("Unable to create project.\n",  err)
			os.Exit(2)
		} else {
			fmt.Print("Created new project " + args[0])
		}
	},
}

func init() {
	//CreateProjectCmd.ValidArgs
	//cmd.createCmd.AddCommand(projectCmd)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// projectCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// projectCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
