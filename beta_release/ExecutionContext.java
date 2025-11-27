import java.util.Map;

/**
 * Provides access to interpreter state during expression evaluation.
 * This allows expression nodes to look up variables without re-parsing.
 */
public class ExecutionContext {
    private final Interpreter interpreter;

    public ExecutionContext(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Double getVariable(String name) {
        return interpreter.getVariables().get(name);
    }

    public void setVariable(String name, double value) {
        interpreter.getVariables().put(name, value);
    }

    public Map<String, double[]> getArrays() {
        return interpreter.getArrays();
    }

    public double[] getArray(String name) {
        return interpreter.getArrays().get(name);
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
