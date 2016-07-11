package com.mohammadi.medical.shifter.constraint;

import android.os.Parcel;

import com.mohammadi.medical.shifter.entities.Resident;

public abstract class PersonalConstraint extends AbstractConstraint
{

    private Resident resident;

    PersonalConstraint()
    {
        setImportance(ConstraintImportance.Important);
    }

    public abstract Constraints getType();

    public Resident getResident()
    {
        return resident;
    }

    public void setResident(Resident resident)
    {
        this.resident = resident;
    }

    @Override
    public PersonalConstraint clone()
    {
        PersonalConstraint constraint = (PersonalConstraint) super.clone();
        if (getResident() != null)
            constraint.setResident(resident.clone());
        return constraint;
    }

}
