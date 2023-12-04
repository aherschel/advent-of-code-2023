import fs from 'fs';

const inputFilename = process.argv[2];

if (!inputFilename || !fs.existsSync(inputFilename)) {
  console.error(`File ${inputFilename} does not exist`);
  process.exit(1);
}

// Rather than figure out the right way to split the chars, just replace multiple whitespaces until it's correct
const numberListFromText = (numberText) => numberText
  .trim()
  .replace('  ', ' ')
  .replace('  ', ' ')
  .replace('  ', ' ')
  .replace('  ', ' ')
  .split(' ')
  .map(val => parseInt(val))
  .filter(val => val !== NaN && val !== null && val !== undefined);

const gameRows = fs.readFileSync(inputFilename, 'utf8').split('\n');

const results = gameRows.map((line) => {
  const [winningNumbersText, gameNumbersText] = line.split(':')[1].split('|');
  const winningNumberSet = new Set(numberListFromText(winningNumbersText));
  const matchCount = numberListFromText(gameNumbersText).filter(number => winningNumberSet.has(number)).length;
  const score = matchCount >= 1 ? 2 ** (matchCount - 1) : 0;
  return { score, matchCount };
});

// Part 1
const score = results.map(({ score }) => score).reduce((a, b) => a + b);
console.log(`Produced Score: ${score}`);

// Part 2
const hitCounts = new Array(gameRows.length).fill(1);
for (let i = 0; i < hitCounts.length; i++) {
  for (let j = 1; j <= results[i].matchCount; j++) {
    hitCounts[i + j] += hitCounts[i];
  }
}
console.log(`CardCount: ${hitCounts.reduce((a, b) => a + b)}`);
