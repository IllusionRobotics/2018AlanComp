/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
/**
 * NOTE: Same as practice bot code due to failing at github, will add components later.
 **/
package org.usfirst.frc.team5852.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {

	private static final String kDefaultAuto  = "Default";
	private static final String kBaselineAuto = "BaselineAuto";
	private static final String kSwitchAuto   = "SwitchAuto";
	private static final String kEncoderTestLeft  = "EncoderTestLeft";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	String gameData;

	//Speed Controllers

	Spark frontLeft  = new Spark(0);
	Spark rearLeft   = new Spark(1);
	Spark frontRight = new Spark(2);
	Spark rearRight  = new Spark(3);
	SpeedControllerGroup left  = new SpeedControllerGroup(frontLeft, rearLeft);
	SpeedControllerGroup right = new SpeedControllerGroup(frontRight, rearRight);

	//Intake
	Spark intake     = new Spark(4);
	//Compressor c = new Compressor(0);

	//Solenoid4Arm
	//DoubleSolenoid Grabnoid = new DoubleSolenoid(0,1);


	/**Note: Not sure how our extra mechanical components are going to be, 
	how they're named etc. */
	//Drivetrain
	DifferentialDrive drive = new DifferentialDrive(left, right);

	//Joystick
	Joystick Joy = new Joystick(0);

	//Buttons
	int Xaxis   = 0;
	int Yaxis   = 1;
	int buttonretract = 3;
	int buttonexpand = 4;

	//Encoders
	Encoder encoderleft  = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	Encoder encoderright = new Encoder(2, 3, false, Encoder.EncodingType.k4X);

	public static final double wheelDiameter = 6;
	public static final double pulsePerRevolution = 2048;
	public static final double encoderGearRatio = 1;
	public static final double gearRatio = 12.75/1;
	public static final double Fudgefactor = 1.0;
	final double distanceperpulse = Math.PI*wheelDiameter/pulsePerRevolution / encoderGearRatio/gearRatio * Fudgefactor;

	//Sticking with FGPA, not messing with unless we get a bigger sense of what to do

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
		m_chooser.addObject("EncoderTestLeft", kEncoderTestLeft);
		SmartDashboard.putData("Auto choices", m_chooser);
		encoderleft.setMaxPeriod(1);
		encoderleft.setDistancePerPulse(distanceperpulse);
		encoderright.setMaxPeriod(1);
		encoderright.setDistancePerPulse(distanceperpulse);
		encoderleft.reset();
		encoderright.reset();
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
		encoderleft.reset();
		encoderright.reset();	
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
					for (int a = 0; a < 200000; a++)
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
		case kEncoderTestLeft:
			encoderleft.reset();
			encoderright.reset();
			while (isAutonomous() && isEnabled())
			{	
				System.out.println(encoderright.getDistance());
				System.out.println(encoderleft.getDistance());
				if(gameData.charAt(0) == 'L')
				{
					if (encoderleft.getDistance() < 6 && encoderright.getDistance() > -6)
						//encoderleft values at distance 6 is around 8100
						//encoderright values at distance -6 is around -8350
					{
						drive.tankDrive(0.66, 0.67);
					}
					else if (encoderright.getDistance() > -7.5)
						//turns go in increments of 1.5 for kitbot
					{
						drive.tankDrive(-1, 1);
					}
					//after turn above, encoder right reads around -8.25/.26, encoder left reads around 4.57
					else if (encoderleft.getDistance() < 12.07 && encoderright.getDistance() > -15.76)
						//Added abt. 7.5 feet.
					{
						drive.tankDrive(0.66, 0.67);
					}
					//after drive above, encoders read at abt. 12.35 and -16.36
					else if (encoderleft.getDistance() < 13.35)
					{
						drive.tankDrive(1, -1);
					}
					//after turn above, encoders read around 14.3 & -14.9
					else if (encoderleft.getDistance() < 17.3 && encoderright.getDistance() > -17.9)
						//3 ft
					{
						drive.tankDrive(0.66, 0.67);
					}
					//after drive, 17.65 & -18.50
					else if (encoderleft.getDistance() < 19.15)
					{
						drive.tankDrive(-1, 1);
					}
					else
					{
						drive.tankDrive(0, 0);
					}
				}
			}
			break;
		case kDefaultAuto:
		default:
			// Put default auto code here Encoder TEST
			break; 
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() 
	{
		//encoderright.reset();
		//encoderleft.reset();
		while(isOperatorControl() && isEnabled())
		{
			SmartDashboard.putNumber("Encoder Right", encoderright.getDistance());
			SmartDashboard.putNumber("Encoder Left", encoderleft.getDistance());
			drive.arcadeDrive(-Joy.getY(), Joy.getX());

			//c.setClosedLoopControl(true);

			//Grabnoid.set(DoubleSolenoid.Value.kOff);

			//if(Joy.getRawButton(buttonretract))
			/**{
				Grabnoid.set(DoubleSolenoid.Value.kForward);
			}
			if(Joy.getRawButton(buttonexpand))
			{
				Grabnoid.set(DoubleSolenoid.Value.kReverse);
			}
			 */
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
