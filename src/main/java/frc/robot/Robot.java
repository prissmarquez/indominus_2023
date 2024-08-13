// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Talon;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //chasis
  CANSparkMax chasis_rightFront = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax chasis_leftFront = new CANSparkMax(2, MotorType.kBrushed);
  CANSparkMax chasis_rightBack = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax chasis_leftBack = new CANSparkMax(4, MotorType.kBrushed);
  DifferentialDrive chasis = new DifferentialDrive(chasis_leftFront, chasis_rightFront);
  Solenoid solenoidvelocidades = new Solenoid(PneumaticsModuleType.REVPH, 1);
  RelativeEncoder chasis_rightEncoder = chasis_rightFront.getEncoder();
  RelativeEncoder chasis_leftEncoder = chasis_leftFront.getEncoder();

  // CONTROLES//
  Joystick driver = new Joystick(1);
  Joystick placer = new Joystick(0);
  boolean  isArcade = true; 

  // navx
  AHRS navX = new AHRS(SPI.Port.kMXP);

  // Garra
  CANSparkMax pivoteoMotorDerecha = new CANSparkMax(5, MotorType.kBrushless);
  CANSparkMax pivoteoMotorIzquierda = new CANSparkMax(6, MotorType.kBrushless);
  CANSparkMax insideMotor = new CANSparkMax(7, MotorType.kBrushless);
  Talon redLine = new Talon(8);
  Talon redLine1 = new Talon(9);

  RelativeEncoder garraEncoder = pivoteoMotorDerecha.getEncoder();

  Timer tiempo = new Timer();
  boolean cubo = false;

  DigitalInput limit = new DigitalInput(1);

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // INITIALIZE
    chasis_rightBack.follow(chasis_rightFront);
    chasis_leftBack.follow(chasis_leftFront);

    chasis_leftEncoder.setPosition(0);
    chasis_rightEncoder.setPosition(0);

    solenoidvelocidades.set(false);
    navX.reset();

    pivoteoMotorDerecha.follow(pivoteoMotorIzquierda);
    garraEncoder.setPosition(0);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    tiempo.start();

  }

  @Override
  public void autonomousPeriodic() {
    if (tiempo.get() < 1) {
      if (garraEncoder.getPosition() < 75){
        pivoteoMotorDerecha.set(0.5);
      }

      else if (garraEncoder.getPosition() > 75) {
        pivoteoMotorDerecha.set(-0.5);
      }

      else {
      redLine.set(-0.5);
      redLine1.set(0.5);
       if (tiempo.get() > 0.075) {
        insideMotor.set(0.5);
      }
      }
    }

    else if (tiempo.get() >= 1 && tiempo.get() < 5) {
      if(garraEncoder.getPosition() > 0) {
        pivoteoMotorDerecha.set(-0.5);
      }
      redLine.set(0);
      redLine1.set(0);
      insideMotor.set(0);
    }

    else if (tiempo.get() >= 5 && tiempo.get() < 13) {
      chasis.arcadeDrive(0.5, 0);
    }

    else if (tiempo.get() >= 13 && tiempo.get() < 15) {
      if (Math.abs(navX.getYaw() - 180) > 5) {
        chasis.arcadeDrive(0, 0.5);
      }
    }

  }

  @Override
  public void teleopInit() {
    tiempo.start();
  }

  @Override
  public void teleopPeriodic() {

    // Cahsis 4 motes y es tanque, 1 selonoide, control 1
    if (driver.getRawButton(1)) {
      isArcade = false;
    } else if (driver.getRawButton(2)) {
      isArcade = true;
    }

    if (isArcade) {
      chasis.arcadeDrive(driver.getRawAxis(1) * 0.7, driver.getRawAxis(4) * 0.7);
    } else {
      chasis.tankDrive(driver.getRawAxis(1) * 0.7, driver.getRawAxis(5) * 0.7);
    }

    if (driver.getRawButton(2)) {
      solenoidvelocidades.set(true);

    } else if (driver.getRawButton(3)) {
      solenoidvelocidades.set(false);
    }

    if (placer.getRawButton(1)) {

      if (limit.get() == false) {
        if (garraEncoder.getPosition() > 0) {
          pivoteoMotorDerecha.set(-0.5);
        } else {
          pivoteoMotorDerecha.set(0);
        }
        redLine.set(0.5);
        redLine1.set(-0.5);
        insideMotor.set(0.5);
      } else {
        if(garraEncoder.getPosition() < 90){
          pivoteoMotorDerecha.set(0.5);
        }
        else{
          pivoteoMotorDerecha.set(0);
        }
        redLine.set(0);
        redLine1.set(0);
        insideMotor.set(0);
      }

    }

    else if (placer.getRawButton(2)) {
      if (garraEncoder.getPosition() < 75){
        pivoteoMotorDerecha.set(0.5);
      }

      else if (garraEncoder.getPosition() > 75) {
        pivoteoMotorDerecha.set(-0.5);
      }

      else {
        pivoteoMotorDerecha.set(0);
        redLine.set(-0.5);
        redLine1.set(0.5);
        if(tiempo.get() > 0.075){
          insideMotor.set(0.5);
        }
      }
    }

    else {
      redLine.set(0);
      redLine1.set(0);
      insideMotor.set(0);
    }

    // Recoger cubos
    // if (placer.getRawButton(1)) {
    // //garraEncoder.setPosition(0);
    // redLine.set(0.5);
    // redLine1.set(-0.5);
    // insideMotor.set(0.5);
    // }
    // //despues la garra pasa a una posicion de initial
    // //shoot
    // else if (placer.getRawButton(2)){
    // //garraEncoder.setPosition(75);
    // redLine.set(-0.5);
    // redLine1.set(0.5);

    // if (tiempo.get() > 0.075){
    // insideMotor.set(0.5);}
    // }

    // else {
    // redLine.set(0);
    // redLine1.set(0);
    // insideMotor.set(0);

    // }

    /*
     * if(cubo){
     * garraEncoder.setPosition(90);
     * }
     */

 // if (placer.getRawButton(3) && limit.get() == true) {
   //   garraEncoder.setPosition(90);
   // }

  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
