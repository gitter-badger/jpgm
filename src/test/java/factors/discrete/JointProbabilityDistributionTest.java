package factors.discrete;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

class JointProbabilityDistributionTest {
  private final double threshold = 10e-8;
  String[] variables = new String[]{"I", "D", "G"};
  int[] cardinality = new int[]{2, 2, 3};
  double[] values = new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.0224, 0.0056,
      0.06, 0.036, 0.024
  };

  JointProbabilityDistribution jpd = null;

  @BeforeEach void setUp() {
    jpd = new JointProbabilityDistribution(variables, cardinality, values);
  }

  @Test void testThrowsWhenValuesDontSumToOne() {
    Assertions.assertNotEquals(null, jpd);
    Assertions.assertThrows(RuntimeException.class, () -> {
      new JointProbabilityDistribution(variables, cardinality, new double[12]);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      new JointProbabilityDistribution(variables, cardinality,
          IntStream.range(0, 12).mapToDouble(Double::new).toArray());
    });
  }

  @Test void testCopy() {
    DiscreteFactor result = (DiscreteFactor)jpd.copy();

    Assertions.assertArrayEquals(result.getScope(), jpd.getScope());
    Assertions.assertArrayEquals(result.getCardinality(), jpd.getCardinality());
    Assertions.assertArrayEquals(result.getValues(), jpd.getValues(), threshold);
  }

  @Test void testNormalize() {
    jpd.normalize(true);

    double actualValueSum = Arrays.stream(jpd.getValues()).sum();

    Assertions.assertEquals(1.0, actualValueSum, threshold);
  }

  @Test void testReduce() {
    double[] iReduction = new double[]{0.252, 0.0224, 0.0056, 0.06, 0.036, 0.024};
    double iSum = Arrays.stream(iReduction).sum();
    double[] expectedValues = Arrays.stream(iReduction)
        .map(v -> v / iSum)
        .toArray();
    List<Pair<String, Integer>> reduceList = new ArrayList<>();
    reduceList.add(Pair.of("I", 1));

    jpd.reduce(reduceList, true);
    double actualValueSum = Arrays.stream(jpd.getValues()).sum();

    Assertions.assertArrayEquals(new String[]{"D", "G"}, jpd.getScope());
    Assertions.assertArrayEquals(new int[]{2, 3}, jpd.getCardinality());
    Assertions.assertArrayEquals(expectedValues, jpd.getValues(), threshold);
    Assertions.assertEquals(1.0, actualValueSum, threshold);
  }

  @Test void testMarginalize() {
    double[] g_marginalized = new double[]{0.42, 0.18, 0.28, 0.12};

    jpd.marginalize(new String[]{"G"}, true);
    double actualValueSum = Arrays.stream(jpd.getValues()).sum();

    Assertions.assertArrayEquals(new String[]{"I", "D"}, jpd.getScope());
    Assertions.assertArrayEquals(new int[]{2, 2}, jpd.getCardinality());
    Assertions.assertArrayEquals(g_marginalized, jpd.getValues(), threshold);
    Assertions.assertEquals(1.0, actualValueSum, threshold);
  }
}
