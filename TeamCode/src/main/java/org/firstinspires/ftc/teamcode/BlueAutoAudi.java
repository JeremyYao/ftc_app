package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Sahithi and Harshini on 11/1/17.
 */
@Autonomous (name = "BlueAutoAudi", group = "Auto")
@Disabled
public class BlueAutoAudi extends LinearOpMode
{
    private TankBase robot;
    private NewRobot newRobot;
    public void runOpMode()
    {
        float center = 0;//placeholder distances
        float x = 0; //placeholder distances
        robot = new TankBase(hardwareMap);
        newRobot = new NewRobot(hardwareMap);
        waitForStart();
        //OLD CODE DO NOT USE
        /*Need to identify the cipher picture
        //Spin to knock Jewel out
        robot.pivot_IMU(30, .25);
        robot.pivot(-30, .25);
        //Drive distance based on cipher
        robot.pivot_IMU(90f,.25);
        robot.driveStraight_In(12f,.3);
        //Drop cipher in box
        robot.stopAllMotors();*/

        //Close the door to grab glyph
        //Lift doors to get glyph
        //Lower left arm
        //Sense color of platform
        //Sense color of front jewel
        //If the jewel is the same color as platform, then move backwards; return to original position
        //else if the jewel isn’t the same color; move forward; return to original position
        //else (no color sense); do nothing
        //raise/retract arm
        //decode pictogram (below)

        telemetry.addData("Pos ", newRobot.getGlyphCipher());
        telemetry.update();
        switch (newRobot.getGlyphCipher())
        {
            case 'l': robot.driveStraight_In(30);
                break;
            case 'c': robot.driveStraight_In(36);
                break;
            case 'r': robot.driveStraight_In(42);
                break;
            default: robot.driveStraight_In(36);
                break;
        }
        robot.pivot_IMU(90, .25);
        robot.driveStraight_In(16);
        robot.driveStraight_In(8,.25);
        //lower door attachment to ground
        //open door to release glyph
        robot.driveStraight_In(-2);
        robot.stopAllMotors();
    }

}