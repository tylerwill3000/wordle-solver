package io.github.tylersharpe.wordle

import groovy.transform.CompileStatic

@CompileStatic
class WordleSolver {
    private static final Set<Character> ALPHABET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.toCharArray().toSet()

    private Trie trie
    private List<ColumnState> columnStates

    WordleSolver(Set<String> dictionary) {
        this.trie = new Trie(dictionary)
        this.columnStates = (1..5).collect {
            new ColumnState(
                letter: null,
                candidates: new HashSet<>(ALPHABET),
                misplaced: new HashSet<Character>()
            )
        }
    }

    List<String> processGuess(String guessResultStr) {
        List<Guess> guesses = parseGuesses(guessResultStr)

        columnStates.eachWithIndex { ColumnState columnState, int columnIndex ->
            if (columnState.letter) {
                return
            }

            Guess guess = guesses[columnIndex]
            switch (guess.result) {
                case Guess.Result.CORRECT:
                    columnState.letter = guess.letter
                    columnState.candidates = Set.of(guess.letter)
                    columnStates.each { it.misplaced.remove(guess.letter) }
                    break

                case Guess.Result.INCORRECT:
                    // letter is not in the word IF it's also not currently part of the list of known misplaced letters
                    // if this is the case, remove it from all unsolved positions
                    boolean isMisplaced = columnStates.any { it.misplaced.contains(guess.letter) }
                    if (!isMisplaced) {
                        List<ColumnState> unsolvedColumns = columnStates.findAll { !it.letter }
                        unsolvedColumns.each { it.candidates.remove(guess.letter) }
                    }
                    break

                case Guess.Result.MISPLACED:
                    columnState.candidates.remove(guess.letter)
                    columnState.misplaced.add(guess.letter)
                    break
            }
        }

        Set<String> nextGuesses = new HashSet<>()
        populateNextGuesses(nextGuesses)

        Map<Character, Integer> charCounts =
            nextGuesses.collectMany { it.toCharArray().toList() } .countBy { it } as Map<Character, Integer>

        nextGuesses.sort { String guess ->
            int score = (int) guess.toCharArray().toSet().collect(charCounts.&get).sum()
            score
        }
    }

    private void populateNextGuesses(Set<String> guessesContainer, String currentGuessStr = '') {
        ColumnState columnState = columnStates[currentGuessStr.size()]

        Set<Character> uniqueMisplaced = columnStates.collectMany { it.misplaced } as Set<Character>

        for (char columnCandidate in columnState.candidates) {
            String newGuessStr = currentGuessStr + columnCandidate

            if (newGuessStr.size() == columnStates.size()) {
                if (trie.contains(newGuessStr) && newGuessStr.toCharArray().toSet().containsAll(uniqueMisplaced)) {
                    guessesContainer.add(newGuessStr)
                }
            } else {
                if (trie.anyWordStartsWith(newGuessStr)) {
                    populateNextGuesses(guessesContainer, newGuessStr)
                }
            }
        }
    }

    private static List<Guess> parseGuesses(String guessResultStr) {
        guessResultStr.trim().split(' +').collect { String resultStr ->
            char letter = Character.toUpperCase(resultStr.charAt(0))

            Guess.Result result
            switch (resultStr) {
                case ~/^[A-Za-z]$/:
                    result = Guess.Result.INCORRECT
                    break

                case ~/^[A-Za-z]\.$/:
                    result = Guess.Result.MISPLACED
                    break

                case ~/^[A-Za-z]\.\.$/:
                    result = Guess.Result.CORRECT
                    break

                default:
                    throw new IllegalArgumentException("No matching input pattern found for '$resultStr'")
            }

            new Guess(letter: letter, result: result)
        }
    }

    @CompileStatic
    private static class Guess {
        enum Result { INCORRECT, MISPLACED, CORRECT }

        char letter
        Result result
    }

    @CompileStatic
    private static class ColumnState {
        Character letter // set once the column is correctly guessed
        Set<Character> candidates
        Set<Character> misplaced
    }
}