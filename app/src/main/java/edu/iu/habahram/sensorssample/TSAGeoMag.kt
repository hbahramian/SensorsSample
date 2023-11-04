package edu.iu.habahram.sensorssample

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.io.StreamTokenizer
import java.util.Calendar
import java.util.GregorianCalendar

/*                PUBLIC DOMAIN NOTICE
This program was prepared by Los Alamos National Security, LLC
at Los Alamos National Laboratory (LANL) under contract No.
DE-AC52-06NA25396 with the U.S. Department of Energy (DOE).
All rights in the program are reserved by the DOE and
Los Alamos National Security, LLC.  Permission is granted to the
public to copy and use this software without charge,
provided that this Notice and any statement of authorship are
reproduced on all copies.  Neither the U.S. Government nor LANS
makes any warranty, express or implied, or assumes any liability
or responsibility for the use of this software.
*/
/*           License Statement from the NOAA
The WMM source code is in the public domain and not licensed or
under copyright. The information and software may be used freely
by the public. As required by 17 U.S.C. 403, third parties producing
copyrighted works consisting predominantly of the material produced
by U.S. government agencies must provide notice with such work(s)
identifying the U.S. Government material incorporated and stating
that such material is not subject to copyright protection.
 */
////////////////////////////////////////////////////////////////////////////
//
//GeoMag.java - originally geomag.c
//Ported to Java 1.0.2 by Tim Walker
//tim.walker@worldnet.att.net
//tim@acusat.com
//
//Updated: 1/28/98
//
//Original source geomag.c available at
//http://www.ngdc.noaa.gov/seg/potfld/DoDWMM.html
//
//NOTE: original comments from the geomag.c source file are in ALL CAPS
//Tim's added comments for the port to Java are not
//
////////////////////////////////////////////////////////////////////////////


//import org.apache.log4j.Logger;

//import org.apache.log4j.Logger;
/**
 *
 *
 * Last updated on Jan 6, 2020
 *
 *
 * **NOTE: **Comment out the logger references, and put back in the System.out.println
 * statements if not using log4j in your application. Checks are not made on the method inputs
 * to ensure they are within a valid range.
 *
 *
 *
 *
 * Verified by a JUnit test using the test values distributed with the 2020 epoch update.
 *
 *
 *
 *
 * This is a class to generate the magnetic declination,
 * magnetic field strength and inclination for any point
 * on the earth.  The true bearing = magnetic bearing + declination.
 * This class is adapted from an Applet from the NOAA National Data Center
 * at [ http://www.ngdc.noaa.gov/seg/segd.shtml.](http://www.ngdc.noaa.gov/seg/segd.shtml)
 * None of the calculations
 * were changed.  This class requires an input file named WMM.COF, which
 * must be in the same directory that the application is run from. <br></br>
 * **NOTE:** If the WMM.COF file is missing, the internal fit coefficients
 * for 2020 will be used.
 *
 *
 * Using the correct date, the declination is accurate to about 0.5 degrees.
 *
 *
 *
 *
 * This is the LANL D-3 version of the GeoMagnetic calculator from
 * the NOAA National Data Center at http://www.ngdc.noaa.gov/seg/segd.shtml.
 *
 *
 *
 *
 * Adapted by John St. Ledger, Los Alamos National Laboratory
 * June 25, 1999
 *
 *
 *
 *
 *
 *
 * Version 2 Comments:  The world magnetic model is updated every 5 years.
 * The data for 2000 uses the same algorithm to calculate the magnetic
 * field variables.  The only change is in the spherical harmonic coefficients
 * in the input file.  The input file has been renamed to WMM.COF.  Once again,
 * the date was fixed.  This time to January 1, 2001.  Also, a deprecated
 * constructor for StreamTokenizer was replaced, and the error messages in the catch
 * clause were changed.  Methods to get the field strength and inclination
 * were added.
 *
 *
 *
 *
 * Found out some interesting information about the altitude. The altitude entered
 * for the calculations is the height above the WGS84 spheroid, not height MSL. Using
 * MSL height means that the altitude could be in error by as much as 200 meters.
 * This should not be significant for our applications.
 *
 *
 * **NOTE:** This class is not thread safe.
 *
 * @version 5.9 January 6, 2020
 *
 * Updated the internal coefficients to the 2020 epoch values. Passes the new JUnit tests.
 *
 *
 * References:
 *
 *  * JOHN M. QUINN, DAVID J. KERRIDGE AND DAVID R. BARRACLOUGH,
 * WORLD MAGNETIC CHARTS FOR 1985 - SPHERICAL HARMONIC
 * MODELS OF THE GEOMAGNETIC FIELD AND ITS SECULAR
 * VARIATION, GEOPHYS. J. R. ASTR. SOC. (1986) 87,
 * PP 1143-1157
 *
 *  * DEFENSE MAPPING AGENCY TECHNICAL REPORT, TR 8350.2:
 * DEPARTMENT OF DEFENSE WORLD GEODETIC SYSTEM 1984,
 * SEPT. 30 (1987)
 *
 *  * JOSEPH C. CAIN, ET AL.; A PROPOSED MODEL FOR THE
 * INTERNATIONAL GEOMAGNETIC REFERENCE FIELD - 1965,
 * J. GEOMAG. AND GEOELECT. VOL. 19, NO. 4, PP 335-355
 * (1967) (SEE APPENDIX)
 *
 *  * ALFRED J. ZMUDA, WORLD MAGNETIC SURVEY 1957-1969,
 * INTERNATIONAL ASSOCIATION OF GEOMAGNETISM AND
 * AERONOMY (IAGA) BULLETIN #28, PP 186-188 (1971)
 *
 *  * JOHN M. QUINN, RACHEL J. COLEMAN, MICHAEL R. PECK, AND
 * STEPHEN E. LAUBER; THE JOINT US/UK 1990 EPOCH
 * WORLD MAGNETIC MODEL, TECHNICAL REPORT NO. 304,
 * NAVAL OCEANOGRAPHIC OFFICE (1991)
 *
 *  * JOHN M. QUINN, RACHEL J. COLEMAN, DONALD L. SHIEL, AND
 * JOHN M. NIGRO; THE JOINT US/UK 1995 EPOCH WORLD
 * MAGNETIC MODEL, TECHNICAL REPORT NO. 314, NAVAL
 * OCEANOGRAPHIC OFFICE (1995)
 *
 *
 *
 *
 *
 * WMM-2000 is a National Imagery and Mapping Agency (NIMA) standard
 * product. It is covered under NIMA Military Specification:
 * MIL-W-89500 (1993).
 *
 *
 * For information on the use and applicability of this product contact
 *
 *
 * DIRECTOR<br></br>
 * NATIONAL IMAGERY AND MAPPING AGENCY/HEADQUARTERS<br></br>
 * ATTN: CODE P33<br></br>
 * 12310 SUNRISE VALLEY DRIVE<br></br>
 * RESTON, VA 20191-3449<br></br>
 * (703) 264-3002<br></br>
 *
 *
 *
 * The FORTRAN version of GEOMAG PROGRAMMED BY:
 *
 *
 * JOHN M. QUINN  7/19/90<br></br>
 * FLEET PRODUCTS DIVISION, CODE N342<br></br>
 * NAVAL OCEANOGRAPHIC OFFICE (NAVOCEANO)<br></br>
 * STENNIS SPACE CENTER (SSC), MS 39522-5001<br></br>
 * USA<br></br>
 * PHONE:   COM:  (601) 688-5828<br></br>
 * AV:        485-5828<br></br>
 * FAX:  (601) 688-5521<br></br>
 *
 *
 * NOW AT:
 *
 *
 * GEOMAGNETICS GROUP<br></br>
 * U. S. GEOLOGICAL SURVEY   MS 966<br></br>
 * FEDERAL CENTER<br></br>
 * DENVER, CO   80225-0046<br></br>
 * USA<br></br>
 * PHONE:   COM: (303) 273-8475<br></br>
 * FAX: (303) 273-8600<br></br>
 * EMAIL:   quinn@ghtmail.cr.usgs.gov<br></br>
 */
class TSAGeoMag {
    /** A logger for this class. Every class MUST have this field, if you want to log from this class.
     * The class name is the fully qualified class name of the class, such as java.lang.String. If you're not going
     * to use log4j, then comment all references to the logger, and uncomment the System.***.println statements. */
    //private static Logger logger = Logger.getLogger(TSAGeoMag.class);
    //variables for magnetic calculations ////////////////////////////////////
    //
    // Variables were identified in geomag.for, the FORTRAN
    // version of the geomag calculator.
    /**
     * The input string array which contains each line of input for the
     * wmm.cof input file.  Added so that all data was internal, so that
     * applications do not have to mess with carrying around a data file.
     * In the TSAGeoMag Class, the columns in this file are as follows:
     * n, m,      gnm,      hnm,       dgnm,      dhnm
     */
    private val input = arrayOf(
        "   2020.0            WMM-2020        12/10/2019",
        "  1  0  -29404.5       0.0        6.7        0.0",
        "  1  1   -1450.7    4652.9        7.7      -25.1",
        "  2  0   -2500.0       0.0      -11.5        0.0",
        "  2  1    2982.0   -2991.6       -7.1      -30.2",
        "  2  2    1676.8    -734.8       -2.2      -23.9",
        "  3  0    1363.9       0.0        2.8        0.0",
        "  3  1   -2381.0     -82.2       -6.2        5.7",
        "  3  2    1236.2     241.8        3.4       -1.0",
        "  3  3     525.7    -542.9      -12.2        1.1",
        "  4  0     903.1       0.0       -1.1        0.0",
        "  4  1     809.4     282.0       -1.6        0.2",
        "  4  2      86.2    -158.4       -6.0        6.9",
        "  4  3    -309.4     199.8        5.4        3.7",
        "  4  4      47.9    -350.1       -5.5       -5.6",
        "  5  0    -234.4       0.0       -0.3        0.0",
        "  5  1     363.1      47.7        0.6        0.1",
        "  5  2     187.8     208.4       -0.7        2.5",
        "  5  3    -140.7    -121.3        0.1       -0.9",
        "  5  4    -151.2      32.2        1.2        3.0",
        "  5  5      13.7      99.1        1.0        0.5",
        "  6  0      65.9       0.0       -0.6        0.0",
        "  6  1      65.6     -19.1       -0.4        0.1",
        "  6  2      73.0      25.0        0.5       -1.8",
        "  6  3    -121.5      52.7        1.4       -1.4",
        "  6  4     -36.2     -64.4       -1.4        0.9",
        "  6  5      13.5       9.0       -0.0        0.1",
        "  6  6     -64.7      68.1        0.8        1.0",
        "  7  0      80.6       0.0       -0.1        0.0",
        "  7  1     -76.8     -51.4       -0.3        0.5",
        "  7  2      -8.3     -16.8       -0.1        0.6",
        "  7  3      56.5       2.3        0.7       -0.7",
        "  7  4      15.8      23.5        0.2       -0.2",
        "  7  5       6.4      -2.2       -0.5       -1.2",
        "  7  6      -7.2     -27.2       -0.8        0.2",
        "  7  7       9.8      -1.9        1.0        0.3",
        "  8  0      23.6       0.0       -0.1        0.0",
        "  8  1       9.8       8.4        0.1       -0.3",
        "  8  2     -17.5     -15.3       -0.1        0.7",
        "  8  3      -0.4      12.8        0.5       -0.2",
        "  8  4     -21.1     -11.8       -0.1        0.5",
        "  8  5      15.3      14.9        0.4       -0.3",
        "  8  6      13.7       3.6        0.5       -0.5",
        "  8  7     -16.5      -6.9        0.0        0.4",
        "  8  8      -0.3       2.8        0.4        0.1",
        "  9  0       5.0       0.0       -0.1        0.0",
        "  9  1       8.2     -23.3       -0.2       -0.3",
        "  9  2       2.9      11.1       -0.0        0.2",
        "  9  3      -1.4       9.8        0.4       -0.4",
        "  9  4      -1.1      -5.1       -0.3        0.4",
        "  9  5     -13.3      -6.2       -0.0        0.1",
        "  9  6       1.1       7.8        0.3       -0.0",
        "  9  7       8.9       0.4       -0.0       -0.2",
        "  9  8      -9.3      -1.5       -0.0        0.5",
        "  9  9     -11.9       9.7       -0.4        0.2",
        " 10  0      -1.9       0.0        0.0        0.0",
        " 10  1      -6.2       3.4       -0.0       -0.0",
        " 10  2      -0.1      -0.2       -0.0        0.1",
        " 10  3       1.7       3.5        0.2       -0.3",
        " 10  4      -0.9       4.8       -0.1        0.1",
        " 10  5       0.6      -8.6       -0.2       -0.2",
        " 10  6      -0.9      -0.1       -0.0        0.1",
        " 10  7       1.9      -4.2       -0.1       -0.0",
        " 10  8       1.4      -3.4       -0.2       -0.1",
        " 10  9      -2.4      -0.1       -0.1        0.2",
        " 10 10      -3.9      -8.8       -0.0       -0.0",
        " 11  0       3.0       0.0       -0.0        0.0",
        " 11  1      -1.4      -0.0       -0.1       -0.0",
        " 11  2      -2.5       2.6       -0.0        0.1",
        " 11  3       2.4      -0.5        0.0        0.0",
        " 11  4      -0.9      -0.4       -0.0        0.2",
        " 11  5       0.3       0.6       -0.1       -0.0",
        " 11  6      -0.7      -0.2        0.0        0.0",
        " 11  7      -0.1      -1.7       -0.0        0.1",
        " 11  8       1.4      -1.6       -0.1       -0.0",
        " 11  9      -0.6      -3.0       -0.1       -0.1",
        " 11 10       0.2      -2.0       -0.1        0.0",
        " 11 11       3.1      -2.6       -0.1       -0.0",
        " 12  0      -2.0       0.0        0.0        0.0",
        " 12  1      -0.1      -1.2       -0.0       -0.0",
        " 12  2       0.5       0.5       -0.0        0.0",
        " 12  3       1.3       1.3        0.0       -0.1",
        " 12  4      -1.2      -1.8       -0.0        0.1",
        " 12  5       0.7       0.1       -0.0       -0.0",
        " 12  6       0.3       0.7        0.0        0.0",
        " 12  7       0.5      -0.1       -0.0       -0.0",
        " 12  8      -0.2       0.6        0.0        0.1",
        " 12  9      -0.5       0.2       -0.0       -0.0",
        " 12 10       0.1      -0.9       -0.0       -0.0",
        " 12 11      -1.1      -0.0       -0.0        0.0",
        " 12 12      -0.3       0.5       -0.1       -0.1"
    )

    /**
     * Geodetic altitude in km. An input,
     * but set to zero in this class.  Changed
     * back to an input in version 5.  If not specified,
     * then is 0.
     */
    private var alt = 0.0

    /**
     * Geodetic latitude in deg.  An input.
     */
    private var glat = 0.0

    /**
     * Geodetic longitude in deg.  An input.
     */
    private var glon = 0.0

    /**
     * Time in decimal years.  An input.
     */
    private var time = 0.0

    /**
     * Geomagnetic declination in deg.
     * East is positive, West is negative.
     * (The negative of variation.)
     */
    private var dec = 0.0

    /**
     * Geomagnetic inclination in deg.
     * Down is positive, up is negative.
     */
    private var dip = 0.0

    /**
     * Geomagnetic total intensity, in nano Teslas.
     */
    private var ti = 0.0
    /**
     * Geomagnetic grid variation, referenced to
     * grid North.  Not calculated or output in version 5.0.
     */
    //private double gv = 0;
    /**
     * The maximum number of degrees of the spherical harmonic model.
     */
    private val maxdeg = 12

    /**
     * The maximum order of spherical harmonic model.
     */
    private var maxord = 0

    /**
     * Added in version 5.  In earlier versions the date for the calculation was held as a
     * constant.  Now the default date is set to 2.5 years plus the epoch read from the
     * input file.
     */
    private var defaultDate = 2022.5

    /**
     * Added in version 5.  In earlier versions the altitude for the calculation was held as a
     * constant at 0.  In version 5, if no altitude is specified in the calculation, this
     * altitude is used by default.
     */
    private val defaultAltitude = 0.0

    /**
     * The Gauss coefficients of main geomagnetic model (nt).
     */
    private val c = Array(13) { DoubleArray(13) }

    /**
     * The Gauss coefficients of secular geomagnetic model (nt/yr).
     */
    private val cd = Array(13) { DoubleArray(13) }

    /**
     * The time adjusted geomagnetic gauss coefficients (nt).
     */
    private val tc = Array(13) { DoubleArray(13) }

    /**
     * The theta derivative of p(n,m) (unnormalized).
     */
    private val dp = Array(13) { DoubleArray(13) }

    /**
     * The Schmidt normalization factors.
     */
    private val snorm = DoubleArray(169)

    /**
     * The sine of (m*spherical coord. longitude).
     */
    private val sp = DoubleArray(13)

    /**
     * The cosine of (m*spherical coord. longitude).
     */
    private val cp = DoubleArray(13)
    private val fn = DoubleArray(13)
    private val fm = DoubleArray(13)

    /**
     * The associated Legendre polynomials for m=1 (unnormalized).
     */
    private val pp = DoubleArray(13)
    private val k = Array(13) { DoubleArray(13) }

    /**
     * The variables otime (old time), oalt (old altitude),
     * olat (old latitude), olon (old longitude), are used to
     * store the values used from the previous calculation to
     * save on calculation time if some inputs don't change.
     */
    private var otime = 0.0
    private var oalt = 0.0
    private var olat = 0.0
    private var olon = 0.0

    /**
     * The date in years, for the start of the valid time of the fit coefficients
     */
    private var epoch = 0.0

    /**
     * bx is the north south field intensity
     * by is the east west field intensity
     * bz is the vertical field intensity positive downward
     * bh is the horizontal field intensity
     */
    private var bx = 0.0
    private var by = 0.0
    private var bz = 0.0
    private var bh = 0.0

    /**
     * re is the Mean radius of IAU-66 ellipsoid, in km.
     * a2 is the Semi-major axis of WGS-84 ellipsoid, in km, squared.
     * b2 is the Semi-minor axis of WGS-84 ellipsoid, in km, squared.
     * c2 is c2 = a2 - b2
     * a4 is a2 squared.
     * b4 is b2 squared.
     * c4 is c4 = a4 - b4.
     */
    private var re = 0.0
    private var a2 = 0.0
    private var b2 = 0.0
    private var c2 = 0.0
    private var a4 = 0.0
    private var b4 = 0.0
    private var c4 = 0.0
    private var r = 0.0
    private var d = 0.0
    private var ca = 0.0
    private var sa = 0.0
    private var ct = 0.0
    private var st = 0.0 // even though these only occur in one method, they must be
    // created here, or won't have correct values calculated
    // These values are only recalculated if the altitude changes.
    //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates object by calling initModel().
     */
    init {
        //read model data from file and initialize the GeoMag routine
        initModel()
    }

    /**
     * Reads data from file and initializes magnetic model.  If
     * the file is not present, or an IO exception occurs, then the internal
     * values valid for 2015 will be used. Note that the last line of the
     * WMM.COF file must be 9999... for this method to read in the input
     * file properly.
     */
    private fun initModel() {
        glat = 0.0
        glon = 0.0
        //bOutDated = false;
        //String strModel = new String();
        //String strFile = new String("WMM.COF");
        //		String strFile = new String("wmm-95.dat");

        // INITIALIZE CONSTANTS
        maxord = maxdeg
        sp[0] = 0.0
        pp[0] = 1.0
        snorm[0] = pp[0]
        cp[0] = snorm[0]
        dp[0][0] = 0.0
        /**
         * Semi-major axis of WGS-84 ellipsoid, in km.
         */
        val a = 6378.137

        /**
         * Semi-minor axis of WGS-84 ellipsoid, in km.
         */
        val b = 6356.7523142
        /**
         * Mean radius of IAU-66 ellipsoid, in km.
         */
        re = 6371.2
        a2 = a * a
        b2 = b * b
        c2 = a2 - b2
        a4 = a2 * a2
        b4 = b2 * b2
        c4 = a4 - b4
        try {
            //open data file and parse values
            //InputStream is;
            val `is`: Reader
            val input = javaClass.getResourceAsStream("WMM.COF")
                ?: throw FileNotFoundException("WMM.COF not found")
            `is` = InputStreamReader(input)
            val str = StreamTokenizer(`is`)


            // READ WORLD MAGNETIC MODEL SPHERICAL HARMONIC COEFFICIENTS
            c[0][0] = 0.0
            cd[0][0] = 0.0
            str.nextToken()
            epoch = str.nval
            defaultDate = epoch + 2.5
            //logger.debug("TSAGeoMag Epoch is: " + epoch);
            //logger.debug("TSAGeoMag default date is: " + defaultDate);
            str.nextToken()
            //strModel = str.sval;
            str.nextToken()

            //loop to get data from file
            while (true) {
                str.nextToken()
                if (str.nval >= 9999) // end of file
                    break
                val n = str.nval.toInt()
                str.nextToken()
                val m = str.nval.toInt()
                str.nextToken()
                val gnm = str.nval
                str.nextToken()
                val hnm = str.nval
                str.nextToken()
                val dgnm = str.nval
                str.nextToken()
                val dhnm = str.nval
                if (m <= n) {
                    c[m][n] = gnm
                    cd[m][n] = dgnm
                    if (m != 0) {
                        c[n][m - 1] = hnm
                        cd[n][m - 1] = dhnm
                    }
                }
            } //while(true)
            `is`.close()
        } //try
        // version 2, catch FileNotFound and IO exceptions separately,
        // rather than catching all exceptions.
        // Version 5.4 add logger support, and comment out System.out.println
        catch (e: FileNotFoundException) {
            val msg = "\nNOTICE      NOTICE      NOTICE      \n" +
                    "WMMCOF file not found in TSAGeoMag.InitModel()\n" +
                    "The input file WMM.COF was not found in the same\n" +
                    "directory as the application.\n" +
                    "The magnetic field components are set to internal values.\n"
            //logger.warn(msg, e);

            /*            String message = new String(e.toString());

            System.out.println("\nNOTICE      NOTICE      NOTICE      ");
            System.out.println("Error:  " + message);
            System.out.println("Error in TSAGeoMag.InitModel()");
            System.out.println("The input file WMM.COF was not found in the same");
            System.out.println("directory as the application.");
            System.out.println("The magnetic field components are set to internal values.");
	     */setCoeff()
        } catch (e: IOException) {
            val msg = "\nNOTICE      NOTICE      NOTICE      \n" +
                    "Problem reading the WMMCOF file in TSAGeoMag.InitModel()\n" +
                    "The input file WMM.COF was found, but there was a problem \n" +
                    "reading the data.\n" +
                    "The magnetic field components are set to internal values."

            //logger.warn(msg, e);

            /*            String message = new String(e.toString());
            System.out.println("\nNOTICE      NOTICE      NOTICE      ");
            System.out.println("Error:  " + message);
            System.out.println("Error in TSAGeoMag.InitModel()");
            System.out.println("The input file WMM.COF was found, but there was a problem ");
            System.out.println("reading the data.");
            System.out.println("The magnetic field components are set to internal values.");

	     */setCoeff()
        }
        // CONVERT SCHMIDT NORMALIZED GAUSS COEFFICIENTS TO UNNORMALIZED
        snorm[0] = 1.0
        for (n in 1..maxord) {
            snorm[n] = snorm[n - 1] * (2 * n - 1) / n
            var j = 2
            var m = 0
            val D1 = 1
            var D2 = (n - m + D1) / D1
            while (D2 > 0) {
                k[m][n] =
                    ((n - 1) * (n - 1) - m * m).toDouble() / ((2 * n - 1) * (2 * n - 3)).toDouble()
                if (m > 0) {
                    val flnmj = (n - m + 1) * j / (n + m).toDouble()
                    snorm[n + m * 13] = snorm[n + (m - 1) * 13] * Math.sqrt(flnmj)
                    j = 1
                    c[n][m - 1] = snorm[n + m * 13] * c[n][m - 1]
                    cd[n][m - 1] = snorm[n + m * 13] * cd[n][m - 1]
                }
                c[m][n] = snorm[n + m * 13] * c[m][n]
                cd[m][n] = snorm[n + m * 13] * cd[m][n]
                D2--
                m += D1
            }
            fn[n] = (n + 1).toDouble()
            fm[n] = n.toDouble()
        } //for(n...)
        k[1][1] = 0.0
        olon = -1000.0
        olat = olon
        oalt = olat
        otime = oalt
    }

    /**
     *
     * **PURPOSE:**  THIS ROUTINE COMPUTES THE DECLINATION (DEC),
     * INCLINATION (DIP), TOTAL INTENSITY (TI) AND
     * GRID VARIATION (GV - POLAR REGIONS ONLY, REFERENCED
     * TO GRID NORTH OF POLAR STEREOGRAPHIC PROJECTION) OF
     * THE EARTH'S MAGNETIC FIELD IN GEODETIC COORDINATES
     * FROM THE COEFFICIENTS OF THE CURRENT OFFICIAL
     * DEPARTMENT OF DEFENSE (DOD) SPHERICAL HARMONIC WORLD
     * MAGNETIC MODEL (WMM-2010).  THE WMM SERIES OF MODELS IS
     * UPDATED EVERY 5 YEARS ON JANUARY 1'ST OF THOSE YEARS
     * WHICH ARE DIVISIBLE BY 5 (I.E. 1980, 1985, 1990 ETC.)
     * BY THE NAVAL OCEANOGRAPHIC OFFICE IN COOPERATION
     * WITH THE BRITISH GEOLOGICAL SURVEY (BGS).  THE MODEL
     * IS BASED ON GEOMAGNETIC SURVEY MEASUREMENTS FROM
     * AIRCRAFT, SATELLITE AND GEOMAGNETIC OBSERVATORIES.
     *
     *
     *
     *
     *
     * **ACCURACY:**  IN OCEAN AREAS AT THE EARTH'S SURFACE OVER THE
     * ENTIRE 5 YEAR LIFE OF A DEGREE AND ORDER 12
     * SPHERICAL HARMONIC MODEL SUCH AS WMM-95, THE ESTIMATED
     * RMS ERRORS FOR THE VARIOUS MAGENTIC COMPONENTS ARE:
     *
     * DEC  -   0.5 Degrees<br></br>
     * DIP  -   0.5 Degrees<br></br>
     * TI   - 280.0 nanoTeslas (nT)<br></br>
     * GV   -   0.5 Degrees<br></br>
     *
     *
     * OTHER MAGNETIC COMPONENTS THAT CAN BE DERIVED FROM
     * THESE FOUR BY SIMPLE TRIGONOMETRIC RELATIONS WILL
     * HAVE THE FOLLOWING APPROXIMATE ERRORS OVER OCEAN AREAS:
     *
     * X    - 140 nT (North)<br></br>
     * Y    - 140 nT (East)<br></br>
     * Z    - 200 nT (Vertical)  Positive is down<br></br>
     * H    - 200 nT (Horizontal)<br></br>
     *
     *
     * OVER LAND THE RMS ERRORS ARE EXPECTED TO BE SOMEWHAT
     * HIGHER, ALTHOUGH THE RMS ERRORS FOR DEC, DIP AND GV
     * ARE STILL ESTIMATED TO BE LESS THAN 0.5 DEGREE, FOR
     * THE ENTIRE 5-YEAR LIFE OF THE MODEL AT THE EARTH's
     * SURFACE.  THE OTHER COMPONENT ERRORS OVER LAND ARE
     * MORE DIFFICULT TO ESTIMATE AND SO ARE NOT GIVEN.
     *
     *
     *
     *
     * THE ACCURACY AT ANY GIVEN TIME OF ALL FOUR
     * GEOMAGNETIC PARAMETERS DEPENDS ON THE GEOMAGNETIC
     * LATITUDE.  THE ERRORS ARE LEAST AT THE EQUATOR AND
     * GREATEST AT THE MAGNETIC POLES.
     *
     *
     *
     *
     * IT IS VERY IMPORTANT TO NOTE THAT A DEGREE AND
     * ORDER 12 MODEL, SUCH AS WMM-2010 DESCRIBES ONLY
     * THE LONG WAVELENGTH SPATIAL MAGNETIC FLUCTUATIONS
     * DUE TO EARTH'S CORE.  NOT INCLUDED IN THE WMM SERIES
     * MODELS ARE INTERMEDIATE AND SHORT WAVELENGTH
     * SPATIAL FLUCTUATIONS OF THE GEOMAGNETIC FIELD
     * WHICH ORIGINATE IN THE EARTH'S MANTLE AND CRUST.
     * CONSEQUENTLY, ISOLATED ANGULAR ERRORS AT VARIOUS
     * POSITIONS ON THE SURFACE (PRIMARILY OVER LAND, IN
     * CONTINENTAL MARGINS AND OVER OCEANIC SEAMOUNTS,
     * RIDGES AND TRENCHES) OF SEVERAL DEGREES MAY BE
     * EXPECTED. ALSO NOT INCLUDED IN THE MODEL ARE
     * NONSECULAR TEMPORAL FLUCTUATIONS OF THE GEOMAGNETIC
     * FIELD OF MAGNETOSPHERIC AND IONOSPHERIC ORIGIN.
     * DURING MAGNETIC STORMS, TEMPORAL FLUCTUATIONS CAN
     * CAUSE SUBSTANTIAL DEVIATIONS OF THE GEOMAGNETIC
     * FIELD FROM MODEL VALUES.  IN ARCTIC AND ANTARCTIC
     * REGIONS, AS WELL AS IN EQUATORIAL REGIONS, DEVIATIONS
     * FROM MODEL VALUES ARE BOTH FREQUENT AND PERSISTENT.
     *
     *
     *
     *
     * IF THE REQUIRED DECLINATION ACCURACY IS MORE
     * STRINGENT THAN THE WMM SERIES OF MODELS PROVIDE, THEN
     * THE USER IS ADVISED TO REQUEST SPECIAL (REGIONAL OR
     * LOCAL) SURVEYS BE PERFORMED AND MODELS PREPARED BY
     * THE USGS, WHICH OPERATES THE US GEOMAGNETIC
     * OBSERVATORIES.  REQUESTS OF THIS NATURE SHOULD
     * BE MADE THROUGH NIMA AT THE ADDRESS ABOVE.
     *
     *
     *
     *
     *
     *
     *
     *
     * NOTE:  THIS VERSION OF GEOMAG USES THE WMM-2010 GEOMAGNETIC
     * MODEL REFERENCED TO THE WGS-84 GRAVITY MODEL ELLIPSOID
     *
     * @param fLat     The latitude in decimal degrees.
     * @param fLon     The longitude in decimal degrees.
     * @param year     The date as a decimal year.
     * @param altitude The altitude in kilometers.
     */
    private fun calcGeoMag(fLat: Double, fLon: Double, year: Double, altitude: Double) {
        glat = fLat
        glon = fLon
        alt = altitude
        /**
         * The date in decimal years for calculating the magnetic field components.
         */
        time = year
        val dt = time - epoch
        //if (otime < 0.0 && (dt < 0.0 || dt > 5.0))
        //		if(bCurrent){
        //			if (dt < 0.0 || dt > 5.0)
        //				bOutDated = true;
        //			else
        //				bOutDated = false;
        //		}
        val pi = Math.PI
        val dtr = pi / 180.0
        val rlon = glon * dtr
        val rlat = glat * dtr
        val srlon = Math.sin(rlon)
        val srlat = Math.sin(rlat)
        val crlon = Math.cos(rlon)
        val crlat = Math.cos(rlat)
        val srlat2 = srlat * srlat
        val crlat2 = crlat * crlat
        sp[1] = srlon
        cp[1] = crlon

        // CONVERT FROM GEODETIC COORDS. TO SPHERICAL COORDS.
        if (alt != oalt || glat != olat) {
            val q = Math.sqrt(a2 - c2 * srlat2)
            val q1 = alt * q
            val q2 = (q1 + a2) / (q1 + b2) * ((q1 + a2) / (q1 + b2))
            ct = srlat / Math.sqrt(q2 * crlat2 + srlat2)
            st = Math.sqrt(1.0 - ct * ct)
            val r2 = alt * alt + 2.0 * q1 + (a4 - c4 * srlat2) / (q * q)
            r = Math.sqrt(r2)
            d = Math.sqrt(a2 * crlat2 + b2 * srlat2)
            ca = (alt + d) / r
            sa = c2 * crlat * srlat / (r * d)
        }
        if (glon != olon) {
            for (m in 2..maxord) {
                sp[m] = sp[1] * cp[m - 1] + cp[1] * sp[m - 1]
                cp[m] = cp[1] * cp[m - 1] - sp[1] * sp[m - 1]
            }
        }
        val aor = re / r
        var ar = aor * aor
        var br = 0.0
        var bt = 0.0
        var bp = 0.0
        var bpp = 0.0
        for (n in 1..maxord) {
            ar = ar * aor
            var m = 0
            val D3 = 1
            var D4 = (n + m + D3) / D3
            while (D4 > 0) {


                //COMPUTE UNNORMALIZED ASSOCIATED LEGENDRE POLYNOMIALS
                //AND DERIVATIVES VIA RECURSION RELATIONS
                if (alt != oalt || glat != olat) {
                    if (n == m) {
                        snorm[n + m * 13] = st * snorm[n - 1 + (m - 1) * 13]
                        dp[m][n] = st * dp[m - 1][n - 1] + ct * snorm[n - 1 + (m - 1) * 13]
                    }
                    if (n == 1 && m == 0) {
                        snorm[n + m * 13] = ct * snorm[n - 1 + m * 13]
                        dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13]
                    }
                    if (n > 1 && n != m) {
                        if (m > n - 2) snorm[n - 2 + m * 13] = 0.0
                        if (m > n - 2) dp[m][n - 2] = 0.0
                        snorm[n + m * 13] =
                            ct * snorm[n - 1 + m * 13] - k[m][n] * snorm[n - 2 + m * 13]
                        dp[m][n] =
                            ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13] - k[m][n] * dp[m][n - 2]
                    }
                }

                //TIME ADJUST THE GAUSS COEFFICIENTS
                if (time != otime) {
                    tc[m][n] = c[m][n] + dt * cd[m][n]
                    if (m != 0) tc[n][m - 1] = c[n][m - 1] + dt * cd[n][m - 1]
                }

                //ACCUMULATE TERMS OF THE SPHERICAL HARMONIC EXPANSIONS
                var temp1: Double
                var temp2: Double
                val par = ar * snorm[n + m * 13]
                if (m == 0) {
                    temp1 = tc[m][n] * cp[m]
                    temp2 = tc[m][n] * sp[m]
                } else {
                    temp1 = tc[m][n] * cp[m] + tc[n][m - 1] * sp[m]
                    temp2 = tc[m][n] * sp[m] - tc[n][m - 1] * cp[m]
                }
                bt = bt - ar * temp1 * dp[m][n]
                bp += fm[m] * temp2 * par
                br += fn[n] * temp1 * par

                //SPECIAL CASE:  NORTH/SOUTH GEOGRAPHIC POLES
                if (st == 0.0 && m == 1) {
                    if (n == 1) pp[n] = pp[n - 1] else pp[n] = ct * pp[n - 1] - k[m][n] * pp[n - 2]
                    val parp = ar * pp[n]
                    bpp += fm[m] * temp2 * parp
                }
                D4--
                m += D3
            }
        } //for(n...)
        if (st == 0.0) bp = bpp else bp /= st

        //ROTATE MAGNETIC VECTOR COMPONENTS FROM SPHERICAL TO
        //GEODETIC COORDINATES
        // by is the east-west field component
        // bx is the north-south field component
        // bz is the vertical field component.
        bx = -bt * ca - br * sa
        by = bp
        bz = bt * sa - br * ca

        //COMPUTE DECLINATION (DEC), INCLINATION (DIP) AND
        //TOTAL INTENSITY (TI)
        bh = Math.sqrt(bx * bx + by * by)
        ti = Math.sqrt(bh * bh + bz * bz)
        //	Calculate the declination.
        dec = Math.atan2(by, bx) / dtr
        //logger.debug( "Dec is: " + dec );
        dip = Math.atan2(bz, bh) / dtr

        //	This is the variation for grid navigation.
        //	Not used at this time.  See St. Ledger for explanation.
        //COMPUTE MAGNETIC GRID VARIATION IF THE CURRENT
        //GEODETIC POSITION IS IN THE ARCTIC OR ANTARCTIC
        //(I.E. GLAT > +55 DEGREES OR GLAT < -55 DEGREES)
        // Grid North is referenced to the 0 Meridian of a polar
        // stereographic projection.

        //OTHERWISE, SET MAGNETIC GRID VARIATION TO -999.0
        /*
         gv = -999.0;
         if (Math.abs(glat) >= 55.){
         if (glat > 0.0 && glon >= 0.0)
         gv = dec-glon;
         if (glat > 0.0 && glon < 0.0)
         gv = dec + Math.abs(glon);
         if (glat < 0.0 && glon >= 0.0)
         gv = dec+glon;
         if (glat < 0.0 && glon < 0.0)
         gv = dec - Math.abs(glon);
         if (gv > +180.0)
         gv -= 360.0;
         if (gv < -180.0)
         gv += 360.0;
         }
	 */otime = time
        oalt = alt
        olat = glat
        olon = glon
    }

    /**
     * Returns the declination from the Department of
     * Defense geomagnetic model and data, in degrees.  The
     * magnetic heading + declination = true heading. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     * (True heading + variation = magnetic heading.)
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The declination in degrees.
     */
    fun getDeclination(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return dec
    }

    /**
     * Returns the declination from the Department of
     * Defense geomagnetic model and data, in degrees.  The
     * magnetic heading + declination = true heading.
     * (True heading + variation = magnetic heading.)
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     The date as a decimal year.
     * @param altitude The altitude in kilometers.
     * @return The declination in degrees.
     */
    fun getDeclination(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return dec
    }

    /**
     * Returns the magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return Magnetic field strength in nano Tesla.
     */
    fun getIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return ti
    }

    /**
     * Returns the magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return Magnetic field strength in nano Tesla.
     */
    fun getIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return ti
    }

    /**
     * Returns the horizontal magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The horizontal magnetic field strength in nano Tesla.
     */
    fun getHorizontalIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bh
    }

    /**
     * Returns the horizontal magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return The horizontal magnetic field strength in nano Tesla.
     */
    fun getHorizontalIntensity(
        dlat: Double,
        dlong: Double,
        year: Double,
        altitude: Double
    ): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bh
    }

    /**
     * Returns the vertical magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The vertical magnetic field strength in nano Tesla.
     */
    fun getVerticalIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bz
    }

    /**
     * Returns the vertical magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return The vertical magnetic field strength in nano Tesla.
     */
    fun getVerticalIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bz
    }

    /**
     * Returns the northerly magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The northerly component of the magnetic field strength in nano Tesla.
     */
    fun getNorthIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bx
    }

    /**
     * Returns the northerly magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return The northerly component of the magnetic field strength in nano Tesla.
     */
    fun getNorthIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bx
    }

    /**
     * Returns the easterly magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla. The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The easterly component of the magnetic field strength in nano Tesla.
     */
    fun getEastIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return by
    }

    /**
     * Returns the easterly magnetic field intensity from the
     * Department of Defense geomagnetic model and data
     * in nano Tesla.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return The easterly component of the magnetic field strength in nano Tesla.
     */
    fun getEastIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return by
    }

    /**
     * Returns the magnetic field dip angle from the
     * Department of Defense geomagnetic model and data,
     * in degrees.  The date and
     * altitude are the defaults, of half way through the valid
     * 5 year period, and 0 elevation.
     *
     * @param dlong Longitude in decimal degrees.
     * @param dlat  Latitude in decimal degrees.
     * @return The magnetic field dip angle, in degrees.
     */
    fun getDipAngle(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return dip
    }

    /**
     * Returns the magnetic field dip angle from the
     * Department of Defense geomagnetic model and data,
     * in degrees.
     *
     * @param dlong    Longitude in decimal degrees.
     * @param dlat     Latitude in decimal degrees.
     * @param year     Date of the calculation in decimal years.
     * @param altitude Altitude of the calculation in kilometers.
     * @return The magnetic field dip angle, in degrees.
     */
    fun getDipAngle(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return dip
    }

    /**
     * This method sets the input data to the internal fit coefficents.
     * If there is an exception reading the input file WMM.COF, these values
     * are used.
     *
     *
     * NOTE:  This method is not tested by the JUnit test, unless the WMM.COF file
     * is missing.
     */
    private fun setCoeff() {
        c[0][0] = 0.0
        cd[0][0] = 0.0
        epoch = input[0].trim { it <= ' ' }.split("[\\s]+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toDouble()
        defaultDate = epoch + 2.5
        var tokens: Array<String>

        //loop to get data from internal values
        for (i in 1 until input.size) {
            tokens =
                input[i].trim { it <= ' ' }.split("[\\s]+".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val n = tokens[0].toInt()
            val m = tokens[1].toInt()
            val gnm = tokens[2].toDouble()
            val hnm = tokens[3].toDouble()
            val dgnm = tokens[4].toDouble()
            val dhnm = tokens[5].toDouble()
            if (m <= n) {
                c[m][n] = gnm
                cd[m][n] = dgnm
                if (m != 0) {
                    c[n][m - 1] = hnm
                    cd[n][m - 1] = dhnm
                }
            }
        }
    }

    /**
     *
     *
     * Given a Gregorian Calendar object, this returns the decimal year
     * value for the calendar, accurate to the day of the input calendar.
     * The hours, minutes, and seconds of the date are ignored.
     *
     *
     *
     *
     * If the input Gregorian Calendar is new GregorianCalendar(2012, 6, 1), all of
     * the first of July is counted, and this returns 2012.5. (183 days out of 366)
     *
     *
     *
     *
     * If the input Gregorian Calendar is new GregorianCalendar(2010, 0, 0), the first
     * of January is not counted, and this returns 2010.0
     *
     *
     *
     * @param cal Has the date (year, month, and day of the month)
     * @return The date in decimal years
     */
    fun decimalYear(cal: GregorianCalendar): Double {
        val year = cal[Calendar.YEAR]
        val daysInYear: Double
        daysInYear = if (cal.isLeapYear(year)) {
            366.0
        } else {
            365.0
        }
        return year + cal[Calendar.DAY_OF_YEAR] / daysInYear
    }
}