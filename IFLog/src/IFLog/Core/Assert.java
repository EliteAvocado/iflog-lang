package IFLog.Core;

/**
 * custom asserts
 */

public final class Assert
{
    public static void Check(final boolean test_case, final String err_msg)
    {
        // do nothing if test is valid
        if (test_case) return;

        Logger.LogError(err_msg);

        System.exit(1);
    }

    public static <T> void Equals(final T first, final T second, final String err_msg)
    {
        if (first != null)
            Check(first.equals(second), err_msg);
        else if (second != null)
            Check(false, err_msg);
    }

    public static <T> void Unequals(final T first, final T second, final String err_msg)
    {
        if (first != null)
            Check(!first.equals(second), err_msg);
        else if (second == null)
            Check(false, err_msg);
    }

    // so we don't accidentally instantiate it
    private Assert() {}
}
