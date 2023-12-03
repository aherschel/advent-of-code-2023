import fs from 'fs';

if (process.argv.length < 3) {
  console.error('Input filename is required');
  process.exit(1);
}

const inputFilename = process.argv[2];

// Check if the file with name inputFilename exists in the local directory
if (!fs.existsSync(inputFilename)) {
  console.error(`File ${inputFilename} does not exist`);
  process.exit(1);
}

const fileContents = fs.readFileSync(inputFilename, 'utf8');

const lines = fileContents.split('\n');

// there are two approaches I can think to this problem (I'm sure there are more)
// the first is a flood-fill algo, where we search for characters, then seek outward for numbers and track a 'visited' list.
// the second is to find all the numbers with a set of bounding coordinates, and then find all the characters with their coords, then iterate over each number and compare if there's a character within 1 char of it's bounding coords

const numbers = [];
lines.forEach((line, row) => {
  let start = -1;
  let end = -1;
  line.split('').forEach((char, index) => {
    if (char >= '0' && char <= '9') {
      if (start === -1) start = index;
      end = index;
    } else if (start !== -1 && end !== -1) {
      const value = parseInt(line.substring(start, end + 1));
      numbers.push({ value, row, start, end });
      start = -1;
      end = -1;
    }
  });
});

const charLocations = [];
const specialChars = new Set(['#', '$', '%', '&', '*', '+', '-', '/', '=', '@']);
lines.forEach((line, row) => {
  line.split('').forEach((char, col) => {
    if (specialChars.has(char)) charLocations.push({ row, col });
  });
});

const gearLocations = [];
lines.forEach((line, row) => {
  line.split('').forEach((char, col) => {
    if (char === '*') gearLocations.push({ row, col });
  });
});

let neighboringValueSum = 0;
numbers.forEach(({ value, row, start, end }) => {
  if ([
    { row, col: start - 1 },
    { row, col: end + 1 },
    { row: row - 1, col: start - 1 },
    { row: row + 1, col: start - 1 },
    { row: row - 1, col: end + 1 },
    { row: row + 1, col: end + 1 },
    ...[...Array(end - start + 1).keys()].map(v => ({ row: row - 1, col: v + start })),
    ...[...Array(end - start + 1).keys()].map(v => ({ row: row + 1, col: v + start })),
  ].some(({ row: slotRow, col: slotCol }) => charLocations.some(({ row: charRow, col: charCol }) => slotCol === charCol && slotRow === charRow))) {
    neighboringValueSum += value;
  }
});
console.log(`Neighboring value sum: ${neighboringValueSum}`);

let gearRatioValue = 0;
gearLocations.forEach(({ row, col }) => {
  const values = [
    { row,          col: col - 1 },
    { row,          col: col + 1 },
    { row: row - 1, col: col - 1 },
    { row: row + 1, col: col - 1 },
    { row: row - 1, col: col + 1 },
    { row: row + 1, col: col + 1 },
    { row: row - 1, col          },
    { row: row + 1, col          },
  ].map(({ row: gearRow, col: gearCol }) => numbers.find(({ row: numRow, start, end }) => numRow === gearRow && gearCol >= start && gearCol <= end)?.value)
   .filter(value => value);
  // This is a hack, but intended to quickly dedupe the fact that I'm hitting numbers if they overlap multiple times.
  const dedupedValues = [...new Set(values)];
  if (dedupedValues.length === 2) gearRatioValue += (dedupedValues[0] * dedupedValues[1]);
});
console.log(`Gear ratio value: ${gearRatioValue}`);
