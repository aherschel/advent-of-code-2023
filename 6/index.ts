import fs from 'fs';

type Race = {
  time: number;
  distance: number;
};

const loadRaces = (): Race[] => {
  const inputFilename = process.argv[2];
  if (!inputFilename || !fs.existsSync(inputFilename)) {
    console.error(`File ${inputFilename} does not exist`);
    process.exit(1);
  }
  const [timeRow, distanceRow] = fs.readFileSync(inputFilename, 'utf8').split('\n');
  const times = [...timeRow.matchAll(/\d+/g)].map(match => parseInt(match[0]));
  const distances = [...distanceRow.matchAll(/\d+/g)].map(match => parseInt(match[0]));
  return times.map((time, index) => ({ time, distance: distances[index] }));
};

const loadRacesAsSingle = (): Race[] => {
  const inputFilename = process.argv[2];
  if (!inputFilename || !fs.existsSync(inputFilename)) {
    console.error(`File ${inputFilename} does not exist`);
    process.exit(1);
  }
  const [timeRow, distanceRow] = fs.readFileSync(inputFilename, 'utf8').split('\n');
  return [{
    time: parseInt([...timeRow.matchAll(/\d+/g)].map(match => match[0].trim()).reduce((a, b) => a.concat(b))),
    distance: parseInt([...distanceRow.matchAll(/\d+/g)].map(match => match[0].trim()).reduce((a, b) => a.concat(b))),
  }];
};

const binarySearchFirstWinningDistance = (race: Race): number => {
  let min = 0;
  let max = race.time;
  while (min < max) {
    const mid = Math.floor((min + max) / 2);
    const currDistance = (race.time - mid) * mid;
    if (currDistance > race.distance) {
      max = mid;
    } else {
      min = mid + 1;
    }
  }
  return min;
};

const main = (loadAsSingleRace: boolean) => {
  const races = loadAsSingleRace ? loadRacesAsSingle() : loadRaces();
  const scores = races.map(race => {
    const firstWinningIndex = binarySearchFirstWinningDistance(race);
    const score = race.time - (firstWinningIndex * 2) + 1;
    return { ...race, firstWinningIndex, score };
  });
  const score = scores.map(({ score }) => score).reduce((a, b) => a * b);
  console.log(`Got Race and Scores: ${JSON.stringify(scores, null, 4)}`);
  console.log(`Got Score: ${score}`);
  process.exit(0);
};

main(true);
