# expMCS

Java Monte Carlo simulation code for binary alloy cluster-expansion style models.

## Project layout
- `expMCS.02.00/src/debug/RunEXPMCS.java` - demo entry point
- `expMCS.02.00/src/mcSampler/` - sampler and statistics
- `expMCS.02.00/src/phase/` - phase abstractions and FCC/BCC implementations

## Requirements
- JDK 8+ (project metadata indicates JavaSE-1.8)

## Compile and run from terminal

```bash
javac -d /tmp/expmcs-build $(find expMCS.02.00/src -name "*.java")
java -cp /tmp/expmcs-build debug.RunEXPMCS
