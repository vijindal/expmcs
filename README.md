# expMCS

`expMCS` is a Java Monte Carlo simulation project for binary alloy cluster-expansion style models.

This project currently contains legacy Java sources under `expMCS.02.00/src` and a runnable entry point in `debug.RunEXPMCS`.

## What we completed so far

1. **Project review document**: `PROJECT_REVIEW.md`
2. **Detailed Gradle onboarding guide**: `GRADLE_SETUP.md`
3. **Working local flow** (NetBeans + terminal) confirmed by you

## Next step after Gradle setup

Now that Gradle setup is done, the next practical step is to standardize day-to-day commands and workflow:

1. Build with Gradle wrapper
2. Run the main simulation entry point
3. Create small PRs for each improvement

## Run commands

### If Gradle wrapper is available

From repository root:

```bash
./gradlew clean build
./gradlew run
```

On Windows CMD/PowerShell:

```powershell
.\gradlew.bat clean build
.\gradlew.bat run
```

### Fallback (direct javac/java)

If wrapper is not available yet, you can still compile and run directly:

```bash
javac -d /tmp/expmcs-build $(rg --files expMCS.02.00/src | tr '\n' ' ')
java -cp /tmp/expmcs-build debug.RunEXPMCS
```

## Recommended short roadmap

- **Step 1 (done):** repository onboarding docs
- **Step 2 (done):** Gradle setup guidance
- **Step 3 (current):** standardize build/run via Gradle wrapper
- **Step 4 (next):** add first smoke test (fast sanity check)
- **Step 5:** add quick-run mode (smaller MC steps for development)
- **Step 6:** package for distribution

## Branch workflow (repeat for each task)

```bash
git checkout master
git pull origin master
git checkout -b <task-branch>
# make changes
git add <files>
git commit -m "<clear message>"
git push -u origin <task-branch>
```

Then open a PR on GitHub and merge after review.

Then sync local:
git checkout master
git pull origin master
git branch -d <task-branch>
