package factors.discrete;

import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for DiscreteFactor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
class DiscreteFactorTest {
  private final double threshold = 10e-8;
  String[] variables = new String[]{"I", "D", "G"};
  int[] cardinality = new int[]{2, 2, 3};
  double[] values = new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.0224, 0.0056,
      0.06, 0.036, 0.024
  };

  DiscreteFactor discreteFactor = null;

  @BeforeEach void setUp() {
    discreteFactor = new DiscreteFactor(variables, cardinality, values);
  }

  @Test void testValidFactor() {
    String[] tooFewVars = new String[]{"I", "D"};
    int[] tooManyCard = new int[]{2, 2, 3, 4};
    double[] incorrectValueSize = new double[9];

    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(tooFewVars, cardinality, values);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(variables, tooManyCard, values);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(variables, cardinality, incorrectValueSize);
    });
  }

  @Test void testGetScope() {
    Assertions.assertArrayEquals(variables, discreteFactor.getScope());
  }

  @Test void testGetVariableAssignmentByName() {
    String[] expectedI = new String[]{"0", "1"};
    String[] expectedG = new String[]{"0", "1", "2"};

    Assertions.assertArrayEquals(expectedI, discreteFactor.getAssignment("I"));
    Assertions.assertArrayEquals(expectedG, discreteFactor.getAssignment("G"));
  }

  @Test void testGetVariableAssignmentByIndex() {
    String[] expectedI = new String[]{"0", "1"};
    String[] expectedG = new String[]{"0", "1", "2"};

    Assertions.assertArrayEquals(expectedI, discreteFactor.getAssignment(0));
    Assertions.assertArrayEquals(expectedG, discreteFactor.getAssignment(2));
  }

  @Test void testNormalize() {
    discreteFactor.normalize(true);

    double actualValueSum = Arrays.stream(discreteFactor.getValues()).sum();

    Assertions.assertEquals(1.0, actualValueSum, threshold);
  }

  @Test void testReduce() {
    double[] iReduction = new double[]{0.252, 0.0224, 0.0056, 0.06, 0.036, 0.024};
    List<Pair<String, Integer>> reduceList = new ArrayList<>();
    reduceList.add(Pair.of("I", 1));

    discreteFactor.reduce(reduceList, true);

    Assertions.assertArrayEquals(new String[]{"D", "G"}, discreteFactor.getScope());
    Assertions.assertArrayEquals(new int[]{2, 3}, discreteFactor.getCardinality());
    Assertions.assertArrayEquals(iReduction, discreteFactor.getValues(), threshold);
  }

  @Test void testMultipleReduction() {
    double[] dgReduction = new double[]{0.126, 0.024};
    List<Pair<String, Integer>> reduceList = new ArrayList<>();
    reduceList.add(Pair.of("D", 1));
    reduceList.add(Pair.of("G", 2));

    discreteFactor.reduce(reduceList, true);

    Assertions.assertArrayEquals(new String[]{"I"}, discreteFactor.getScope());
    Assertions.assertArrayEquals(new int[]{2}, discreteFactor.getCardinality());
    Assertions.assertArrayEquals(dgReduction, discreteFactor.getValues(), threshold);
  }

  @Test void testMarginalize() {
    double[] g_marginalized = new double[]{0.42, 0.18, 0.28, 0.12};

    discreteFactor.marginalize(new String[]{"G"}, true);

    Assertions.assertArrayEquals(new String[]{"I", "D"}, discreteFactor.getScope());
    Assertions.assertArrayEquals(new int[]{2, 2}, discreteFactor.getCardinality());
    Assertions.assertArrayEquals(g_marginalized, discreteFactor.getValues(), threshold);
  }

  /**
   * TODO parameterize test
   */
  @Test void testMultipleMarginalized() {
    double[] gd_marginalized = new double[]{0.60, 0.40};

    discreteFactor.marginalize(new String[]{"G", "D"}, true);

    Assertions.assertArrayEquals(new String[]{"I"}, discreteFactor.getScope());
    Assertions.assertArrayEquals(new int[]{2}, discreteFactor.getCardinality());
    Assertions.assertArrayEquals(gd_marginalized, discreteFactor.getValues(), threshold);
  }
}
