package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "TensorFlowDecisionTree")
public class TensorFlowDecisionTree extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        VuforiaFunctions vuforiaFunctions = new VuforiaFunctions(this, hardwareMap);
        waitForStart();

        while (opModeIsActive())
        {
/*
            ArrayList<Recognition> closest = vuforiaFunctions.getTwoClosestRecognitions();
            if(closest != null)
            {
                if ( closest.size() != 0)
                {
                    for (Recognition temp : closest)
                    {
                        telemetry.addData(temp.toString(), null);
                        telemetry.addData("New line", null);
                    }
                }
            }
            */
            telemetry.addData("SEe ", vuforiaFunctions.getPositionOfGoldInTwoObjects());
            telemetry.update();
        }
    }
}
