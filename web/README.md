# ARMS (Billing)

This is the WEB for the ABMS application.

&copy; 2016 IDS North America

## Requirements

- Git
- NPM
- Bower
- Typings (important to have version 1.4.0 or greater)

## Installation

Note: If there are steps missing, or problems that occur during set up, please record and we will update this readme

1. git clone this project
2. An .env file needs to be set up or provided for the initial run, there is a file called `.env.example`.  For local environment you can rename this to `.env`
3. run `npm install`, `bower install`, `typings install` inside the directory
3. For first time launch, run `gulp build:config` for production, `gulp serve:config` for development.  Subsequent launches can be just `gulp serve`

## Build & development

ARMS is currently running under AngularJS 1.5.3

Running `gulp serve` will set up a browser tab that will reload
when SCSS, Unit tests or Typescript files are changed.

Majority of file changes will occur under `./src/app/partials`, these are modules

There is a folder called `angular-ids-project`.  This is a shared components directory that is from an IDS git project called `angular-ids-project`.  Currently being shared with [carbon cut](https://git.idscorporation.ca/eureka/carbon-cut-web) through `git subtree`.  There is a good chance you won't need to make changes here, but if so, please understand subtree before making changes here

## Committing Code Guidelines

[When it comes to UI formatting, please refer to the Contribution guide](CONTRIBUTING.md)

Committing to `develop` or `master` branch is prohibited

[Follow these guidelines when branching off develop](http://nvie.com/posts/a-successful-git-branching-model/).  If you are working on creating for example, flight movements, you might call your branch `feature/flight-movements`.  Ensure you branch off the correct branch when starting new changes (unless you know what you're doing).

[Commit messages should explain what has changed, and why it changed](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#type).  It should be in present tense, it should start lower case, it should not end with a period.

example:

fix(flight-movements): save button click event calls flight movement endpoint

## Pull/Merge Request Guidelines

- Review your own code, "test code" or "commented out code" is [frowned upon](https://medium.com/@kentcdodds/please-don-t-commit-commented-out-code-53d0b5b26d5f#.nauwqapyd)
- Create a pull request in GitLab, assign another user to review the code
- Any issue the assignee will comment on the code line
- They will thumbs up it (and possibly leave a funny emoji)
- At this point, if the build succeeds, you can click the "accept merge request" and your code will be in develop
 
We are using a free version of Gitlab where we can't enforce a proper code review, meaning you can merge it in without someone reviewing it.  Do not do this!

Some reasons for a Merge Request to not be quickly merged in:

- Karma/Protractor tests fail
- Build fails
- Bad code
- Bad commit messages


## Testing

Running `gulp test:auto` will run the unit tests with karma and reload on changes.  Note, typescript interfaces may continue giving warnings, you must exit out of karma with gulp test:

## Protractor

If Protractor is installed, you can run `protractor protractor.conf.js`.

## Advanced development

There is an IDS project called [generator-ids-angular](https://git.idscorporation.ca/ids/generator-ids-angular).  This Yeoman generator can create a lot of code for you with a simple command, saving you a lot of time.
