Remote -> origin -> master -> Checkout  (switches to master branch)
master -> Update Project (pulls all changes from server to master branch)
Person-branch -> Checkout (switch to person branch)
Remote -> origin -> master -> Merge 'origin/master' into 'Person-branch' (Adds changes from master to Person-branch)
Person-branch (top of screen) -> Push (pushes all changes from master to 'Person-branch')

Do some code, commit changes to Person-branch

Remote -> origin -> master -> Checkout  (switches to master branch)
Remote -> origin -> Person-branch -> Merge 'Person-branch' into 'origin/master' (adds person branch changes to master branch)
master (top of screen) -> Push (pushes all commits to master branch)
