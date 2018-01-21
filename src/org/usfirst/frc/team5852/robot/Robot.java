/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5852.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {

	private static final String kDefaultAuto  = "Default";
	private static final String kBaselineAuto = "BaselineAuto";
	private static final String kSwitchAuto   = "SwitchAuto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	String gameData;

	//Speed Controllers

	Talon frontLeft  = new Talon(0);
	Talon frontRight = new Talon(1);
	Talon rearLeft   = new Talon(2);
	Talon rearRight  = new Talon(3);
	SpeedControllerGroup left = new SpeedControllerGroup(frontLeft, rearLeft);
	SpeedControllerGroup right = new SpeedControllerGroup(frontRight, rearRight);

	//Intake
	Talon intake     = new Talon(4);
	Compressor c = new Compressor(0);
	//Solenoid4Arm
	DoubleSolenoid Grabnoid = new DoubleSolenoid(0,1);
	
	
	/**Note: Not sure how our extra mechanical components are going to be, 
	how they're named etc. */
	//Drivetrain
	DifferentialDrive drive = new DifferentialDrive(left, right);

	//Joystick
	Joystick Joy = new Joystick(0);

	//Buttons
	int Xaxis   = 0;
	int Yaxis   = 1;
	int buttonA = 2;
	int buttonretract = 3;
	int buttonexpand = 4;
	
	/**	This was from last year's autonomous, don't know if it will be used this year once we get encoders/do vision processing
	 * int centerx = 320;
		int centery = 240;
	 */

	/** Currently keeping the same joystick system for right now, bar extra components
	 * from scrappy, like the climber.
	 */

	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("BaselineAuto", kBaselineAuto);
		m_chooser.addObject("SwitchAuto", kSwitchAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);

		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
		case kBaselineAuto:
			while (isAutonomous() && isEnabled())
			{
				for (int a = 0; a < 90000; a++)
				{
					drive.tankDrive(0.5, 0.5);
				}
				Timer.delay(13);
				break;
			}
		case kSwitchAuto:
			while (isAutonomous() && isEnabled())
			{
				if(gameData.charAt(0) == 'L')
				{
					//Put left auto code here
					//Go forward
					for (int a = 0; a < 20000; a++)
					{
						drive.tankDrive(0.5, 0.5);
					}	
					Timer.delay(1);
					//Spin Left
					for (int b = 0; b < 20000;  b++)
					{
						drive.tankDrive(-0.5, 0.5);
					}
					//Go Forward
					for (int c = 0; c < 40000; c++)
					{
						drive.tankDrive(0.5, 0.5);
					}
					Timer.delay(1);
					//Spin back Right
					for (int d = 0; d < 16500;  d++)
					{
						drive.tankDrive(0.5,-0.5);
					}
					Timer.delay(1);
					//Go forward to fence
					for (int e = 0; e < 55000;  e++)
					{
						drive.tankDrive(0.5, 0.5);
					}
					Timer.delay(10);
					break;
				}
				else {
					//Put right auto code here
					//Go forward
					for (int a = 0; a < 20000; a++)
					{
						drive.tankDrive(0.5, 0.5);
					}	
					Timer.delay(1);
					//Spin Right
					for (int b = 0; b < 20000;  b++)
					{
						drive.tankDrive(0.5, -0.5);
					}
					//Go Forward
					for (int c = 0; c < 40000; c++)
					{
						drive.tankDrive(0.5, 0.5);
					}
					//Spin back Right
					Timer.delay(1);
					for (int d = 0; d < 16500;  d++)
					{
						drive.tankDrive(-0.5, 0.5);
					}
					Timer.delay(1);
					//Go forward to fence
					for (int e = 0; e < 55000;  e++)
					{
						drive.tankDrive(0.5, 0.5);
					}
					Timer.delay(10);
					break;
				}
			}

		case kDefaultAuto:
		default:
			// Put default auto code here
			break; 
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() 
	{
		while(isOperatorControl() && isEnabled())
		{
			drive.arcadeDrive(-Joy.getY(), Joy.getX());

			c.setClosedLoopControl(true);
			Grabnoid.set(DoubleSolenoid.Value.kOff);
			
			if(Joy.getRawButton(buttonexpand));
			{
				Grabnoid.set(DoubleSolenoid.Value.kForward);
			}
			
			//NOTE: No idea how the pneumatics is wired, so I don't know that the retraction & extraction side does.
			if(Joy.getRawButton(buttonretract));
			{
				Grabnoid.set(DoubleSolenoid.Value.kReverse);
			}
			/**BIGNOTE: Remember to code in extra components once the team comes to consensus
			 * 
			 */
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
