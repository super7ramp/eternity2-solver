# eternity2-solver

This is a solver library for (reduced versions of) the [Eternity II](https://en.wikipedia.org/wiki/Eternity_II_puzzle)
puzzle. It is written in Java and uses a SAT solver ([Sat4j](http://www.sat4j.org/)).

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>re.belv</groupId>
    <artifactId>eternity2-solver</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

Here is an example of how to use the library:

```java
// The list of pieces (id, north color, east color, south color, west color).
final var pieces = new Piece[]{
                new Piece(0, 0, 1, 2, 3),
                new Piece(1, 0, 1, 2, 3),
                new Piece(2, 0, 1, 2, 3),
                new Piece(3, 0, 1, 2, 3),
        };

// The board is a 2x2 grid. The bottom right piece is fixed.
final var initialBoard = new Piece[2][2];
initialBoard[1][1] = pieces[1].rotate(Piece.Rotation.PLUS_90);

// Instantiate the solver and solve the game.
final var solver = new Solver();
final Iterator<Piece[][]> solutions = solver.solve(pieces, initialBoard);
while(solutions.hasNext()){
    final Piece[][] solution = solutions.next();
    System.out.println(Arrays.deepToString(solution));
}
```