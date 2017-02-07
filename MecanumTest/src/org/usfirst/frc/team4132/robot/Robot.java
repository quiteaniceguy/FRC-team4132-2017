package org.usfirst.frc.team4132.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	final float GUMBALLSPEED=.35f, DRIVESPEED=.6f, TURNSPEED=.6f;
	
	
	//kMXP port is port on roborio
	AHRS ahrs;
	
	private Spark fl_Wheel;
	private Spark fr_Wheel;
	private Spark bl_Wheel;
	private Spark br_Wheel;
	
	private Talon shooter;

	Joystick controller;
	
	RobotDrive drive;
	
	
	enum DriveAxis{
		DRIVEAXIS, SHOOTERAXIS;
	}
	enum ButtonState{
		EDGE, DOWN, RELEASED;
	}

	DriveAxis currentDriveAxis=DriveAxis.DRIVEAXIS;
	ButtonState currentButtonState=ButtonState.RELEASED;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		
		//must change
		fl_Wheel=new Spark(1);
		fr_Wheel=new Spark(4);
		bl_Wheel=new Spark(2);
		br_Wheel=new Spark(3);
		
		shooter=new Talon(0);
		shooter.setInverted(true);
		
		
		
		ahrs= new AHRS(SerialPort.Port.kMXP);
		
		controller=new Joystick(0);
		
		drive=new RobotDrive(fl_Wheel, bl_Wheel, fr_Wheel, br_Wheel);
		
		
	}

	
	
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		///chooses drive system
		if(currentDriveAxis==DriveAxis.DRIVEAXIS){
			///0 at end of constructor is used for gyro value
			drive.mecanumDrive_Cartesian(controller.getRawAxis(0)*DRIVESPEED, controller.getRawAxis(1)*DRIVESPEED, controller.getRawAxis(4)*TURNSPEED, 0);
		}else{
			drive.mecanumDrive_Cartesian(controller.getRawAxis(0)*DRIVESPEED, controller.getRawAxis(1)*DRIVESPEED, controller.getRawAxis(4)*TURNSPEED, 0);
		}
		
		///gets button state and does according actions
		switch(currentButtonState){
		case RELEASED:
			if(controller.getRawButton(4))
				currentButtonState=ButtonState.EDGE;
			break;
			
		case EDGE:
			//switches to other drive system using opposite axis
			if(currentDriveAxis==DriveAxis.DRIVEAXIS){
				currentDriveAxis=DriveAxis.SHOOTERAXIS;
			}else{
				currentDriveAxis=DriveAxis.DRIVEAXIS;
			}
			
			currentButtonState=ButtonState.DOWN;
			break;
			
		case DOWN: 
			if(!controller.getRawButton(4))
				currentButtonState=ButtonState.RELEASED;
			break;
		}
		
		//shooter
		if(controller.getRawButton(1)){
			shooter.set(GUMBALLSPEED);
			System.out.println("button pressed");
		}
		else{
			shooter.set(0);
			System.out.println("not pressed");
		}
		
		//testing with nav board
		/*
		System.out.println(ahrs.getAngle());
		if(controller.getRawButton(5)){
			ahrs.reset();
			System.out.println("reseted");
		}
		*/
		
		
		
		///testing 
		System.out.println("currentDriveAxis: "+currentDriveAxis);
		System.out.println("currentButtonState: "+currentButtonState);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

