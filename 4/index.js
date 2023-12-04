import fs from 'fs';

const inputFilename = process.argv[2];

if (!inputFilename || !fs.existsSync(inputFilename)) {
  console.error(`File ${inputFilename} does not exist`);
  process.exit(1);
}

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
  
const score = results.map(({ score }) => score).reduce((a, b) => a + b);
console.log(`Produced Score: ${score}`);

const cardQueue = results.map(({ matchCount }, index) => ({ index, matchCount }));
// const seenCards = [...cardQueue];
const hitCounts = new Array(gameRows.length).fill(0);
while (cardQueue.length > 0) {
  const { index, matchCount } = cardQueue.shift();
  hitCounts[index]++;
  for (let i = 1; i <= matchCount; i++) {
    const nextIndex = index + i;
    const nextCard = { index: nextIndex, matchCount: results[nextIndex].matchCount };
    cardQueue.push(nextCard);
    // seenCards.push(nextCard);
  }
}
// seenCards.forEach(({ index }) => hitCounts[index]++);
// console.log(`Accrued Total Card Count: ${seenCards.length}`);
