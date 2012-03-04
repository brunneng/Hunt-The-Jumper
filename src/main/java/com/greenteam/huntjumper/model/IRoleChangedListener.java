package com.greenteam.huntjumper.model;

/**
 * User: GreenTea Date: 04.03.12 Time: 22:34
 */
public interface IRoleChangedListener
{
   public void signalRoleIsChanged(JumperRole oldRole, JumperRole newRole);
}
