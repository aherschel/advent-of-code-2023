import fs from 'fs';

/**
 * Load the input rows using the provided filename argument.
 * @returns a list of strings representing the rows in the input file
 */
const loadInputRows = (): string[] => {
  const inputFilename = process.argv[2];
  if (!inputFilename || !fs.existsSync(inputFilename)) {
    console.error(`File ${inputFilename} does not exist`);
    process.exit(1);
  }
  return fs.readFileSync(inputFilename, 'utf8').split('\n');
};

type MappingDiscriminator = {
  fromType: string;
  toType: string;
};

type RowMapping = {
  destinationRangeStart: number;
  sourceRangeStart: number;
  rangeLength: number;
};

type Mapping = {
  discriminator: MappingDiscriminator;
  rowMappings: RowMapping[];
};

/**
 * Parse the input rows, and extract the 'seeds' as the numbers following the row with seeds at the front,
 * then parse out each individual mapping object.
 * Assumptions: `seeds` row comes first,
 * There is a newline row between mappings
 */
const parseInputRows = (
  inputRows: string[],
): { seeds: number[], mappings: Mapping[] } => {
  let seeds: number[] = [];
  let mappings: Mapping[] = [];
  let discriminator: MappingDiscriminator | null = null;
  let rowMappings: RowMapping[] = [];
  const addMappingAndReset = () => {
    if (discriminator) {
      mappings.push({ discriminator, rowMappings });
      discriminator = null;
      rowMappings = [];
    }
  };
  inputRows.forEach((row) => {
    const numMatch: number[] = [...row.matchAll(/\d+/g)].map(match => parseInt(match[0]));
    const discriminatorMatch = row.match(/(.*)-to-(.*) map:/);
    const discriminatorMatchStrings = discriminatorMatch ? [discriminatorMatch[1], discriminatorMatch[2]] : [];
    if (row.startsWith('seeds:') && numMatch.length > 0) {
      seeds = numMatch;
    }
    else if (discriminatorMatchStrings.length === 2) {
      addMappingAndReset();
      const [fromType, toType] = discriminatorMatchStrings;
      discriminator = { fromType, toType };
    }
    else if (numMatch.length === 3) {
      const [destinationRangeStart, sourceRangeStart, rangeLength] = numMatch;
      rowMappings.push({ destinationRangeStart, sourceRangeStart, rangeLength });
    }
  });
  addMappingAndReset();
  return {
    seeds,
    mappings,
  };
};

/**
 * Map a value through the weird mapping type.
 * This is done by finding the implicit range which matches the value (e.g. sourceRangeStart <= value && soruceRangeStart + rangeLength <= value)
 * and then mapping the value to the destination range (e.g. destinationRangeStart + (value - sourceRangeStart))
 */
const computeMappedValue = (value: number, rowMappings: RowMapping[]): number => {
  const matchingRowMapping = rowMappings.find(rowMapping => rowMapping.sourceRangeStart <= value && rowMapping.sourceRangeStart + rowMapping.rangeLength > value);
  if (!matchingRowMapping) return value;
  return matchingRowMapping.destinationRangeStart + value - matchingRowMapping.sourceRangeStart;
};

/**
 * Given an input 'seed' value, traverse the mappings (which are effectively a degenerate graph)
 * until the 'to location' mapping is discovered and computed, and return the destination value there.
 */
const calculateSeedLocation = (seed: number, mappings: Mapping[]): number => {
  let type = 'seed';
  let value = seed;
  do {
    const nextMapping = mappings.find(mapping => mapping.discriminator.fromType === type);
    if (!nextMapping) {
      throw new Error(`Could not find mapping for type ${type}`);
    }
    const nextValue = computeMappedValue(value, nextMapping.rowMappings);
    value = nextValue;
    type = nextMapping.discriminator.toType;
  } while (type !== 'location')
  return value;
};

const main  = () => {
  const inputRows = loadInputRows();
  console.log(`Loaded ${inputRows.length} rows`);
  const { seeds: seedRanges, mappings } = parseInputRows(inputRows);
  let minLocation = Number.POSITIVE_INFINITY;
  // This is terrible. Initially I took the approach of threading the values through the maps, and that proved to be wrong
  // in the expanded problem set. I just brute forced it though, and rewrote some portions of the logic to use less memory to avoid
  // OOM issues.
  // I didn't feel like rewriting for the second half of the problem, but I think probably what you would end up doing is storing a list of ranges
  // and when you go through a 'mapping' you would basically transpose the ranges, and potentially need to split each range n times to fit into the mapping ranges
  console.log(`There are ${seedRanges.length / 2} ranges to operate on.`);
  let rangeIndex = 0;
  while (seedRanges.length !== 0) {
    const startIndex = seedRanges.shift();
    const length = seedRanges.shift();
    if (!startIndex || !length) {
      throw new Error('Seeds array is not a valid range');
    }
    for (let i = 0; i < length; i++) {
      const seed = i + startIndex;
      const location = calculateSeedLocation(seed, mappings);
      if (location < minLocation) {
        minLocation = location;
      }
    }
    console.log(`Range ${rangeIndex++} processed`);
  }
  console.log(`Computed minLocation: ${minLocation}`);
  process.exit(0);
};

main();
