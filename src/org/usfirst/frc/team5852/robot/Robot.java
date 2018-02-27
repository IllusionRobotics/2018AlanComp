/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5852.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {

	private static final String kDefaultAuto  = "Default";
	private static final String kEncoderCenter= "EncoderCenter";
	private static final String kEncoderLeft  = "EncoderLeft";
	private static final String kEncoderRight = "EncoderRight";
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

	//Limit Switch
	DigitalInput limit = new DigitalInput(4);
	
	//Intake
	Spark intake     = new Spark(4);
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
	int buttonretract = 3;
	int buttonexpand  = 4;
	int buttonup      = 5;
	int buttondown    = 6;

	//gyro
	Gyro gyro1 = new ADXRS450_Gyro();
	double kp = 0.08;
	double angle = gyro1.getAngle();
	int step = 1;

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

	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("EncoderCenter", kEncoderCenter);
		m_chooser.addObject("EncoderLeft", kEncoderLeft);
		m_chooser.addObject("EncoderRight", kEncoderRight);
		SmartDashboard.putData("Auto choices", m_chooser);
		encoderleft.setMaxPeriod(1);
		encoderleft.setDistancePerPulse(distanceperpulse);
		encoderright.setMaxPeriod(1);
		encoderright.setDistancePerPulse(distanceperpulse);
		encoderleft.reset();
		encoderright.reset();
		step = 0;
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
		gyro1.reset();
		step = 1;
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {

		case kEncoderCenter:
			encoderleft.reset();
			encoderright.reset();
			while (isAutonomous() && isEnabled())
			{	
				if(gameData.charAt(0) == 'L')
				{
					if (step == 1)
					{
						while (encoderleft.getDistance() < 6 && encoderright.getDistance() > -6)
						{
							drive.tankDrive(0.69, 0.66);
							//drive.curvatureDrive(0.5,-angle*kp, true);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{

						while (gyro1.getAngle() > -90)
						{
							drive.tankDrive(-0.6, 0.6);
							/**
							 *    NOTE: This formula is something if you wanted a fast to slow turn 
							 * 
							 *angle = gyro1.getAngle();
							{
								drive.tankDrive(-(angle+90)*0.003 - 0.4, (angle+90)*0.003 + 0.4);
							}
							 */
						}
						encoderleft.reset();
						encoderright.reset();
						step = 3;		
					}
					else if (step == 3)
					{
						while(encoderleft.getDistance() < 9 && encoderright.getDistance() > -9)
						{
							drive.tankDrive(0.69, 0.66);
							//angle = ((gyro1.getAngle()+90) / 180);
							//drive.curvatureDrive(0.5,-angle*kp, true);
							
						}
						encoderleft.reset();
						encoderright.reset();
						step = 4;
					}
					else if (step == 4)
					{
						while(gyro1.getAngle() < 0)
						{
							drive.tankDrive(0.6, -0.6);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 5;
					}
					else if (step == 5)
					{
						while(encoderleft.getDistance() < 6 && encoderright.getDistance() > -6)
						{
							drive.tankDrive(0.69, 0.66);
							/**angle = ((gyro1.getAngle()+90) / 180);
							drive.curvatureDrive(0.5,-angle*kp, true);
							 */
						}
						encoderleft.reset();
						encoderright.reset();
						step = 6;
					}
					else if (step == 6)
					{	
						while(gyro1.getAngle() < 90)
						{
							drive.tankDrive(0.6, -0.6);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 7;
					}
					else if (step == 7)
					{
						while(encoderleft.getDistance() < 1 && encoderright.getDistance() > -1)
						{
							drive.tankDrive(0.69, 0.66);
							/**angle = ((gyro1.getAngle()+90) / 180);
							drive.curvatureDrive(0.5,-angle*kp, true);
							 */
							intake.set(-0.3);
						}
						step = 8;
					}
					else if(step == 8)
					{
						Grabnoid.set(DoubleSolenoid.Value.kReverse);
						drive.tankDrive(0, 0);
					}
				}
				else
				{
					if (step == 1)
					{
						while (encoderleft.getDistance() < 6 && encoderright.getDistance() > -6)
						{
							drive.tankDrive(0.69, 0.66);
							//drive.curvatureDrive(0.5,-angle*kp, true);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{

						while (gyro1.getAngle() < 90)
						{
							drive.tankDrive(0.6, -0.6);
							/**
							 *    NOTE: This formula is something if you wanted a fast to slow turn 
							 * 
							 *angle = gyro1.getAngle();
							{
								drive.tankDrive(-(angle+90)*0.003 - 0.4, (angle+90)*0.003 + 0.4);
							}
							 */
						}
						encoderleft.reset();
						encoderright.reset();
						step = 3;		
					}
					else if (step == 3)
					{
						while(encoderleft.getDistance() < 9 && encoderright.getDistance() > -9)
						{
							drive.tankDrive(0.69, 0.66);
							/**angle = ((gyro1.getAngle()+90) / 180);
							drive.curvatureDrive(0.5,-angle*kp, true);
							 */
						}
						encoderleft.reset();
						encoderright.reset();
						step = 4;
					}
					else if (step == 4)
					{
						while(gyro1.getAngle() > 0)
						{
							drive.tankDrive(-0.5, 0.5);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 5;
					}
					else if (step == 5)
					{
						while(encoderleft.getDistance() < 6 && encoderright.getDistance() > -6)
						{
							drive.tankDrive(0.69, 0.66);
							/**angle = ((gyro1.getAngle()+90) / 180);
							drive.curvatureDrive(0.5,-angle*kp, true);
							 */
						}
						encoderleft.reset();
						encoderright.reset();
						step = 6;
					}
					else if (step == 6)
					{	
						while(gyro1.getAngle() > -90)
						{
							drive.tankDrive(-0.6, 0.6);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 7;
					}
					else if (step == 7)
					{
						while(encoderleft.getDistance() < 1 && encoderright.getDistance() > -1)
						{
							drive.tankDrive(0.69, 0.66);
							/**angle = ((gyro1.getAngle()+90) / 180);
							drive.curvatureDrive(0.5,-angle*kp, true);
							 */
							intake.set(-0.5);
						}
						step = 8;
					}
					else if(step == 8)
					{
						Grabnoid.set(DoubleSolenoid.Value.kReverse);
						drive.tankDrive(0, 0);
					}

				}
			}
			break;
		case kEncoderLeft:
			encoderleft.reset();
			encoderright.reset();
			while(isAutonomous() && isEnabled())
			{
				if(gameData.charAt(0) == 'L')
				{
					if (step == 1)
					{
						while(encoderleft.getDistance() < 11 && encoderright.getDistance() > -11)
						{
							drive.tankDrive(0.69, 0.66);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{
						while(gyro1.getAngle() < 90)
						{
							drive.tankDrive(0.6, -0.6);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 3;
					}
					else if (step == 3)
					{
						while(encoderleft.getDistance() < 1 && encoderright.getDistance() > -1)
						{
							drive.tankDrive(0.69, 0.66);
							intake.set(-0.5);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 4;
					}
					else if (step == 4)
					{
						drive.tankDrive(0, 0);
						Grabnoid.set(DoubleSolenoid.Value.kReverse);
					}
				}
				else
				{
					if (step == 1)
					{
						while(encoderleft.getDistance() < 11 && encoderright.getDistance() > -11)
						{
							drive.tankDrive(0.69, 0.66);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{
						drive.tankDrive(0, 0);
					}
				}
			}
		case kEncoderRight:
			encoderleft.reset();
			encoderright.reset();
			while(isAutonomous() && isEnabled())
			{
				if(gameData.charAt(0) == 'R')
				{
					if (step == 1)
					{
						while(encoderleft.getDistance() < 11 && encoderright.getDistance() > -11)
						{
							drive.tankDrive(0.69, 0.66);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{
						while(gyro1.getAngle() > -90)
						{
							drive.tankDrive(-0.6, 0.6);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 3;
					}
					else if (step == 3)
					{
						while(encoderleft.getDistance() < 1 && encoderright.getDistance() > -1)
						{
							drive.tankDrive(0.69, 0.66);
							intake.set(-0.5);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 4;
					}
					else if (step == 4)
					{
						drive.tankDrive(0, 0);
						Grabnoid.set(DoubleSolenoid.Value.kReverse);
					}
				}
				else
				{
					if (step == 1)
					{
						while(encoderleft.getDistance() < 11 && encoderright.getDistance() > -11)
						{
							drive.tankDrive(0.69, 0.66);
						}
						encoderleft.reset();
						encoderright.reset();
						step = 2;
					}
					else if (step == 2)
					{
						drive.tankDrive(0, 0);
					}
				}
			}
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
		step = 0;
		encoderright.reset();
		encoderleft.reset();
		while(isOperatorControl() && isEnabled())
		{
			SmartDashboard.putNumber("Encoder Right", encoderright.getDistance());
			SmartDashboard.putNumber("Encoder Left", encoderleft.getDistance());
			drive.arcadeDrive(-Joy.getY(), Joy.getX());

			//pneumatics
			c.setClosedLoopControl(true);

			Grabnoid.set(DoubleSolenoid.Value.kOff);

			//pneumatics control
			if(Joy.getRawButton(buttonretract))
			{
				Grabnoid.set(DoubleSolenoid.Value.kForward);
			}
			if(Joy.getRawButton(buttonexpand))
			{
				Grabnoid.set(DoubleSolenoid.Value.kReverse);
			}

			//arm control
			if(Joy.getRawButton(buttonup) && Joy.getRawButton(buttondown))
			{
				intake.set(0);
			}
			
			else if(Joy.getRawButton(buttonup))
			{
				intake.set(0.5);
			}	
			else if(limit.get() && Joy.getRawButton(buttondown))
			{
				intake.set(0);
			}
			else if(Joy.getRawButton(buttondown))
			{
				intake.set(-0.5);
			}
			else
			{
				intake.set(0);
			}
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
