# Overview
This is an interactive command-line program which suggests the best words to guess in order to solve the browser game [wordle](https://www.powerlanguage.co.uk/wordle/). Based on personal testing, the program is typically able to guess most words within a maximum of 4 guesses.

# Running
Build the program using `gradlew build`. Then, execute the program by running `java -jar build\libs\wordle-solver.jar`

# Usage
The program is designed to be used in the background while playing the actual game in the browser.
After each guess made in the browser, you must input the result into the program. Each letter of the guess should be separated by 1 or more spaces and appended with '.' if the letter is misplaced (displayed as a yellow tile in the browser) or '..' if the letter is correct (displayed as a green tile in the browser). For example, the following input would be used to express that the letter 'H' is misplaced and the letter 'S' is correct:

```t h. o s.. e```

After inputting the result, the program will output the next word you should guess which it thinks will maximize your ability to narrow down the solution. The program uses an algorithm that ensures it will only suggest words containing all letters currently known to be misplaced and which also exclude all letters currently known to not be in the word. If a point is reached where only a single valid word remains, the program will output the solution and then terminate.

The program uses a custom dictionary of valid five-letter words. It's possible wordle may choose a word that is not in the dictionary. If this is the case, the program will output that it cannot determine a valid word to guess and will prompt you to update the local dictionary with whatever the word ends up being.