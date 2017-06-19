package cmd

import (
	"github.com/spf13/cobra"
	"github.com/ethankhall/version-manager-clients/cli/create"
	"os"
)

// createCmd represents the create command
var createCmd = &cobra.Command{
	Use:   "create",
	Short: "Create a new entry in Crom",
	Long: `Crom has three major elements, Projects, Repositories and Versions.

This command allows you to create all of them. Each will have it's own arguments,
review the help for each individual command.`,
	Run: func(cmd *cobra.Command, args []string) {
		cmd.Help()
		os.Exit(1)
	},
}

func init() {
	RootCmd.AddCommand(createCmd)
	createCmd.MarkPersistentFlagRequired("api-token")
	createCmd.AddCommand(create.CreateProjectCmd)
	createCmd.AddCommand(create.CreateRepositoryCmd)
	createCmd.AddCommand(create.CreateVersionCmd)
}
