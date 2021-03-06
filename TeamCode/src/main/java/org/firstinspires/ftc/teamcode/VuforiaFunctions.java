package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

public class VuforiaFunctions
{
    public static final String VUFORIA_KEY = "AepnoMf/////AAAAGWsPSj5vh0WQpMc0OEApBsgbZVwduMSeEZFjXMlBPW7WiZRgwGXsOTLiGMxL4qjU0MYpZitHxs4E/nOUHseMX+SW0oopu6BnWL3cAqFIptSrdMpy4y6yB3N6l+FPcGFZxzadvRoiOfAuYIu5QMHSeulfQ1XApDhBQ79lNUXv9LZ7bngBI3BEYVB+slmTGHKhRW2NI5fUtF+rLRiou4ZcNir2eZh0OxEW4zAnTnciVB2R28yyHkYz8xJtACm+4heWLdpw/zf66LRpvTGLwkASci7ZkGJp4NrG5Of4C0b3+iq/EeEmX2PiY5lq2fkUE0dejdztmkFWYBW7c/Y+bIYGER/3gt6I8UhAB78cR7p2mOaY";
    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = FRONT;

    private static final float mmTargetHeight = (6) * 25.4f;          // the height of the center of the target image above the floor

    private VuforiaLocalizer vuforia;
    private OpenGLMatrix lastLocation = null;

    private VuforiaTrackable blueRover;
    private VuforiaTrackable redFootprint;
    private VuforiaTrackable frontCraters;
    private VuforiaTrackable backSpace;
    private VuforiaTrackables targetsRoverRuckus;
    private ArrayList<VuforiaTrackable> allTrackables;

    private String currentNameOfTargetSeen = "";
    private VectorF translation;
    private Orientation rotation;

    private TFObjectDetector tfod;
    public static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    public static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    public static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    public static final float HALFFIELDLENGTH_MM = 1828.8f;

    public VuforiaFunctions(OpMode opMode_In, HardwareMap hardwareMap)
    {
        int cameraMonitorViewId = opMode_In.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode_In.hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");
        blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");
        redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");
        frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");
        backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");

        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsRoverRuckus);

        OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                .translation(0, HALFFIELDLENGTH_MM, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90));
        blueRover.setLocation(blueRoverLocationOnField);

        OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                .translation(HALFFIELDLENGTH_MM * 2, HALFFIELDLENGTH_MM, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        redFootprint.setLocation(redFootprintLocationOnField);

        OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                .translation(HALFFIELDLENGTH_MM, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
        frontCraters.setLocation(frontCratersLocationOnField);

        OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                .translation(HALFFIELDLENGTH_MM, HALFFIELDLENGTH_MM * 2, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
        backSpace.setLocation(backSpaceLocationOnField);

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(0, 0, 0)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        CAMERA_CHOICE == FRONT ? 90 : -90, 0, 0));

        for (VuforiaTrackable trackable : allTrackables)
        {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }

        targetsRoverRuckus.activate();

        if (ClassFactory.getInstance().canCreateTFObjectDetector())
            initTfod(hardwareMap);
    }

    private void initTfod(HardwareMap hardwareMap)
    {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);

        if (tfod != null)
        {
            tfod.activate();
        }
    }

    public boolean hasSeenTarget()
    {
        for (VuforiaTrackable trackable : allTrackables)
        {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible())
            {
                currentNameOfTargetSeen = trackable.getName();
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null)
                {
                    lastLocation = robotLocationTransform;
                    translation = robotLocationTransform.getTranslation();
                    rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                }
                return true;
            }
        }
        return false;
    }

    public void updatePositions()
    {
        for (VuforiaTrackable trackable : allTrackables)
        {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible())
            {
                currentNameOfTargetSeen = trackable.getName();
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null)
                {
                    lastLocation = robotLocationTransform;
                    translation = robotLocationTransform.getTranslation();
                    rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                }
            }
        }
    }

    public float getXPosIn()
    {
        return translation.get(0) / 25.4f;
    }

    public float getYPosIn()
    {
        return translation.get(1) / 25.4f;
    }

    public float getYawDeg()
    {
        return rotation.thirdAngle;
    }

    public float getYawDegTo360()
    {
        if (rotation.thirdAngle < 0)
            return rotation.thirdAngle + 360f;
        else
            return rotation.thirdAngle;
    }

    public String getCurrentNameOfTargetSeen()
    {
        return currentNameOfTargetSeen;
    }

    public TFObjectDetector getTfod()
    {
        return tfod;
    }

    public char getPositionOfGoldInTwoObjects()
    {
        ArrayList<Recognition> recognitions = getTwoClosestRecognitions();

        if (recognitions == null || recognitions.size() == 0 || recognitions.size() == 1)
            return '?';
        else
        {
            int numGold = 0;

            for (Recognition temp : recognitions)
            {
                if (temp.getLabel().equals(LABEL_GOLD_MINERAL))
                    numGold++;
            }

            if (numGold >= 2)
            {
                return '?';
            }

            for (int i = 0; i < recognitions.size(); i++)
            {
                if (recognitions.get(i).getLabel().equals(LABEL_GOLD_MINERAL))
                {
                    if (i == 0)
                    {
                        if (recognitions.get(i).getLeft() > recognitions.get(1).getLeft())
                            return 'c';
                        else
                            return 'r';
                    }
                    else
                    {
                        if (recognitions.get(i).getLeft() > recognitions.get(0).getLeft())
                            return 'c';
                        else
                            return 'r';
                    }
                }
            }
        }
        return 'l';
    }

    public ArrayList<Recognition> getTwoClosestRecognitions()
    {
        List<Recognition> allRecs = tfod.getRecognitions();
        ArrayList<Recognition> closeestRecs = new ArrayList<Recognition>();

        if (allRecs == null)
            return null;
        else if (allRecs.size() == 0)
            return null;
        else if (allRecs.size() == 1)
        {
            closeestRecs.add(allRecs.get(0));
            return closeestRecs;
        }
        else if (allRecs.size() >= 2)
        {
            Recognition temp = allRecs.get(0);
            int index = 0;
            for (int i = 1; i < allRecs.size(); i++)
            {
                if (temp.getHeight() > allRecs.get(i).getHeight())
                {
                    temp = allRecs.get(i);
                    index = i;
                }
            }
            closeestRecs.add(temp);
            allRecs.remove(index);

            temp = allRecs.get(0);
            index = 0;
            for (int i = 0; i < allRecs.size(); i++)
            {
                if (temp.getHeight() > allRecs.get(i).getHeight())
                {
                    temp = allRecs.get(i);
                    index = i;
                }
            }
            closeestRecs.add(temp);
            allRecs.remove(index);

            return closeestRecs;
        }
        return null;
    }

    public Recognition getOneClosestRecognition()
    {
        List<Recognition> allRecs = tfod.getRecognitions();

        if (allRecs == null)
            return null;
        else if (allRecs.size() == 0)
            return null;
        else if (allRecs.size() == 1)
            return allRecs.get(0);
        else if (allRecs.size() >= 2)
        {
            Recognition temp = allRecs.get(0);
            for (int i = 1; i < allRecs.size(); i++)
            {
                if (temp.getHeight() > allRecs.get(i).getHeight())
                {
                    temp = allRecs.get(i);
                }
            }
            return temp;
        }
        return null;
    }
}
