package org.usfirst.frc.team4132.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
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
	
	final float GUMBALLSPEED=.25f, DRIVESPEED=1f, TURNSPEED=.6f, SHOOTERSPEED=1f, LIFTERSPEED=.75f, GUMBALLCOUNTPERMILI=.15f, COUNTPERMILITHRESHOLD=.01f; 
	
	int timerCounter=0;
	
	double lastTime;
	double lastCount;
	
	//kMXP port is port on roborio
	AHRS ahrs;
	
	//motors
	private Spark fl_Wheel;
	private Spark fr_Wheel;
	private Spark bl_Wheel;
	private Spark br_Wheel;
	
	private Talon gumballMotor;
	private Spark shooterMotor;
	
	private Talon lifter_one;
	private Talon lifter_two;

	Joystick controller;
	
	RobotDrive drive;
	
	//sensors
	Encoder gumballEncoder;
	
	enum GumballState{
		MOVINGBACKWARDS, NOTPRESSED, MOVING, OVERRIDE, DELAY;
	}
	GumballState currentGumballState=GumballState.NOTPRESSED;
	double gumballTimer=0;
	int gumballFailsInRow=0;
	double vGumballSpeed;
	int nFails=0;
	/**
	 * 
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		//(port, port, invertCouting?, encoder type
		gumballEncoder=new Encoder(7,8, false, Encoder.EncodingType.k1X);
				
				
				
		//sensors && counters
		lastTime=System.currentTimeMillis();
		lastCount=gumballEncoder.getRaw();
		
		//motors
		fl_Wheel=new Spark(7);
		fr_Wheel=new Spark(3);
		bl_Wheel=new Spark(6);
		br_Wheel=new Spark(4);
		
		fl_Wheel.setInverted(true);
		fr_Wheel.setInverted(true);
		br_Wheel.setInverted(true);
		
		gumballMotor=new Talon(1);
		gumballMotor.setInverted(true);
		
		shooterMotor=new Spark(5);
		
		lifter_one=new Talon(2);
		lifter_two=new Talon(0);
		lifter_one.setInverted(true);
		lifter_two.setInverted(true);
		
		
		
		
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
		///0 at end of constructor is used for gyro value
		///axis 0 is x	
		//drive.mecanumDrive_Cartesian(controller.getRawAxis(4)*DRIVESPEED, controller.getRawAxis(1)*DRIVESPEED, controller.getRawAxis(0)*TURNSPEED, 0);
		
		
		

		
		
		
		//gumballMotor
		
		
		///change speed of gumball wheel according to pressure on wheel
		vGumballSpeed=GUMBALLSPEED * (GUMBALLCOUNTPERMILI / ((gumballEncoder.get()-lastCount)/(System.currentTimeMillis()-lastTime)) );
		///sometimes it's set to negative
		vGumballSpeed=Math.abs(vGumballSpeed);
		if(vGumballSpeed>GUMBALLSPEED*2)vGumballSpeed=GUMBALLSPEED*2;
		
		
		
		switch(currentGumballState){
		case NOTPRESSED:
			gumballMotor.set(0);
			if(controller.getRawButton(5)){
				currentGumballState=GumballState.MOVING;
			}
			else if(controller.getRawButton(4)){
				currentGumballState=GumballState.MOVINGBACKWARDS;
			}
			break;
			
		case MOVING:
			gumballMotor.set(vGumballSpeed);
			
			//finds number of failes in a row
			if((gumballEncoder.get()-lastCount)/(System.currentTimeMillis()-lastTime)<COUNTPERMILITHRESHOLD){
				
				gumballFailsInRow++;
			}
			else{
				gumballFailsInRow=0;
			}
			//System.out.println("gumballencoder per mili: "+(gumballEncoder.get()-lastCount)/(System.currentTimeMillis()-lastTime));
			//System.out.println("gumball count per loop: "+(gumballEncoder.get()-lastCount));
			//System.out.println("fails in a row: "+gumballFailsInRow);
			if( controller.getRawButton(5) && gumballFailsInRow>25){
				currentGumballState=GumballState.MOVINGBACKWARDS;
				System.out.println("failed: "+nFails++);
				gumballFailsInRow=0;
			}
			else if(!controller.getRawButton(5)){
				
				currentGumballState=GumballState.NOTPRESSED;
				gumballFailsInRow=0;
				
			}
			System.out.println("rails in a row: "+gumballFailsInRow);
			break;
			
			
		case MOVINGBACKWARDS:
			gumballMotor.set(-GUMBALLSPEED);
			gumballTimer+=System.currentTimeMillis()-lastTime;
			if(gumballTimer>250){
				currentGumballState=GumballState.NOTPRESSED;
				gumballTimer=0;
			}
			break;
			
		case DELAY:
			gumballMotor.set(vGumballSpeed);
			gumballTimer+=System.currentTimeMillis()-lastTime;
			if(gumballTimer>1000 || !controller.getRawButton(6)){
				currentGumballState=GumballState.NOTPRESSED;
				gumballTimer=0;
			}
			
		}
		
		
		
		
		//shooter
		if(controller.getRawButton(6)){
			shooterMotor.set(SHOOTERSPEED);
			//System.out.println("shooting");
		}
		else{
			shooterMotor.set(0);
		}
		
		
		//lifter b button
		if(controller.getRawButton(2)){
			lifter_one.set(LIFTERSPEED);
			lifter_two.set(LIFTERSPEED);
		}else{
			lifter_one.set(0);
			lifter_two.set(0);
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
		
        //double currentTime=System.currentTimeMillis();
        //System.out.println( "milis Time per sixty frames: "+String.valueOf( currentTime-lastTime) ); 
        lastTime=System.currentTimeMillis();
        lastCount=gumballEncoder.get();
        
	
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

