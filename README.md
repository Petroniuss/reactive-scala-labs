# reactive-lab

### Author
Patryk Wojtyczek

## Average concurrency 

- 10k-local: 10^4 / 60 * 2 * 10^(-3) / 1 = 0.33 - https://www.wolframalpha.com/input/?i=10%5E4+%2F+60+*+2+*+10%5E%28-3%29+%2F+1
- 12k-local: 1.2 * 10^4 / 60 * 4 * 10^(-3) / 1 = 0.8 - https://www.wolframalpha.com/input/?i=1.2+*+10%5E4+%2F+60+*+4+*+10%5E%28-3%29+%2F+1
- 10k-cluster: 10^4 / 60 * 25 * 10^(-3) / 3 = 1.38 - https://www.wolframalpha.com/input/?i=10%5E4+%2F+60+*+25+*+10%5E%28-3%29+%2F+3
- 12k-cluster: 1.2 * 10^4 / 60 * 74 * 10^(-3) / 3 = 4.93 - https://www.wolframalpha.com/input/?i=1.2+*+10%5E4+%2F+60+*+74+*+10%5E%28-3%29+%2F+3