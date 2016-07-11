/**
 *
 */
package com.mohammadi.medical.shifter.constraint;

import org.chocosolver.solver.Solver;

import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;

import java.io.Serializable;

/**
 * @author Mohammadi
 */
public abstract class AbstractConstraint implements Cloneable, Serializable
{
    private int                  id;
    private ConstraintImportance importance;
    private int                  cost;
    private String               name;

    public abstract void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables, boolean isKnightShitConstraint);


    @Override
    public AbstractConstraint clone()
    {
        try
        {
            return (AbstractConstraint) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException();
        }
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ConstraintImportance getImportance()
    {
        return importance;
    }

    public void setImportance(ConstraintImportance importance)
    {
        this.importance = importance;
        setCost(importance.getCost());
    }

    public int getCost()
    {
        return cost;
    }

    public void setCost(int cost)
    {
        this.cost = cost;
    }

    public boolean isEditable()
    {
        return true;
    }

    public boolean isDeletable()
    {
        return true;
    }
}
