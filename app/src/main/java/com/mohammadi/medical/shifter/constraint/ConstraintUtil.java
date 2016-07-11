package com.mohammadi.medical.shifter.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConstraintUtil
{

    public static List<List<AbstractConstraint>> getSortedOptionalConstraints(List<AbstractConstraint> constraints)
    {
        List<List<AbstractConstraint>> sortedConstraints = new ArrayList<>();

        List<AbstractConstraint> optionalConstraint = new ArrayList<>();

        for (AbstractConstraint constraint : constraints)
        {
            if (isConstraintOptional(constraint))
            {
                optionalConstraint.add(constraint);
            }
        }

        for (Integer integer : grayCode(optionalConstraint.size()))
        {
            List<AbstractConstraint> list = new ArrayList<>();
            for (int i = 0; i < optionalConstraint.size(); i++)
            {
                if (((integer >> i) & 1) == 1)
                    list.add(optionalConstraint.get(i));
            }
            sortedConstraints.add(list);

        }

        Collections.sort(sortedConstraints, new Comparator<List<AbstractConstraint>>()
        {

            @Override
            public int compare(List<AbstractConstraint> o1, List<AbstractConstraint> o2)
            {
                int o1Point = 0;
                for (AbstractConstraint abstractConstraint : o1)
                {
                    o1Point += abstractConstraint.getCost();
                }
                int o2Point = 0;
                for (AbstractConstraint abstractConstraint : o2)
                {
                    o2Point += abstractConstraint.getCost();
                }

                if (o1Point > o2Point)
                    return 1;
                else if (o1Point < o2Point)
                    return -1;
                return 0;

            }

        });

        return sortedConstraints;
    }

    private static boolean isConstraintOptional(AbstractConstraint constraint)
    {
        return constraint.getImportance() == ConstraintImportance.Optional;
    }

    public static ArrayList<Integer> grayCode(int n)
    {
        ArrayList<Integer> result = new ArrayList<>();
        if (n == 0)
        {
            result.add(0);
            return result;
        }

        result.add(0);
        result.add(1);
        for (int i = 1; i < n; i++)
        {
            ArrayList<Integer> tmp = new ArrayList<>(result);
            Integer a = 1 << i;
            for (int k = result.size() - 1; k >= 0; k--)
            {
                tmp.add(result.get(k) + a);
            }
            result = tmp;
        }
        return result;
    }
}
