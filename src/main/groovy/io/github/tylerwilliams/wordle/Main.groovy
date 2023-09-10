package io.github.tylerwilliams.wordle

import groovy.transform.CompileStatic

import static java.util.stream.Collectors.toSet

@CompileStatic
class Main {

    static void main(String... args) {
        Set<String> dictionary = loadFiveLetterWords()
        WordleSolver solver = new WordleSolver(dictionary)

        new Scanner(System.in).withCloseable { sysIn ->
            playWordle(sysIn, solver)
        }
    }

    private static void playWordle(Scanner sysIn, WordleSolver solver) {
        // 'AROSE' is one of the best first guesses
        println "Next recommended guess: 'AROSE'"

        5.times {
            print "Enter guess result: "

            String guessResult = sysIn.nextLine()
            List<String> nextGuesses = solver.processGuess(guessResult)

            switch (nextGuesses.size()) {
                case 0:
                    println "No valid words found to guess! Update the `five-letter-words.txt` file with whatever the word ends up being"
                    System.exit(1)
                    break

                case 1:
                    println "The word is '${nextGuesses.first()}'"
                    System.exit(0)
                    break

                default:
                    println "Next recommended guess: '${nextGuesses.last()}'"
            }
        }
    }

    private static Set<String> loadFiveLetterWords() {
        Main.classLoader.getResource('five-letter-words.txt')
            .text
            .lines()
            .map(word -> word.toUpperCase())
            .collect(toSet())
    }
}
