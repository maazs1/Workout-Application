package com.evolveworkoutapplication

import android.content.Intent
import java.math.RoundingMode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.acitivity_physique.*
import kotlinx.android.synthetic.main.confirm_dialogue.view.*
import kotlinx.android.synthetic.main.workout_dialogue.view.*
import java.math.BigInteger
import kotlin.math.roundToInt

class physiqueActivity : AppCompatActivity() {

    private var mImgIds: IntArray? = null
    private var topName = ArrayList<String>(4)
    private var bottomName = ArrayList<String>(4)
    private var flag: Int = 0
    private var physique: String? = null
    var heightFeet:Double?=null
    var heightInch:Double?=null
    var username: String?=null
    var gender :String? =null
    var age :String? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_physique)


        val bundle: Bundle? = intent.extras
        username= bundle?.getString("username")
        gender= bundle?.getString("gender")
        age = bundle?.getString("age")


        topName.add("LEAN"); topName.add("FIT"); topName.add("ATHLETIC"); topName.add("STURDY")
        bottomName.add("THIN"); bottomName.add("AVERAGE");bottomName.add("MUSCULAR");bottomName.add("HUSKY/HEAVY")

        if (gender == "Male"){
            maleInitData()
        }

        if (gender == "Female"){
            femaleInitData()
        }

        InitView()

        continuePhysique.setOnClickListener {
            if (flag==0){
                Toast.makeText(this, "Select a Physique", Toast.LENGTH_SHORT).show()
            }
            else{
                checkFlag(gender,username)
                nextActivity()
            }
        }

        backButtonPhysique.setOnClickListener{
            val intent = Intent(this, muscleInformation::class.java)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            startActivity(intent)
            finish()
        }
    }

    private fun nextActivity(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.workout_dialogue, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.noButton.setOnClickListener {
            mAlertDialog.dismiss()
            mAlertDialog.dismiss()
            val intent = Intent(this, begginerStartingPoint::class.java)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            startActivity(intent)

        }

        mDialogView.yesButton.setOnClickListener {
            mAlertDialog.dismiss()
            val intent = Intent(this, startingPoint::class.java)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            startActivity(intent)

        }


    }

    private fun checkFlag(gender: String?, username:String?) {

        when (flag) {
            1 -> physique = "Lean"
            2 -> physique = "Fit"
            3 -> physique = "Athletic"
            4 -> physique = "Stocky"
        }

        calculateGoal(gender, username)
    }
    private fun calculateGoal(gender: String?, username:String?){
        var Lean:Int
        var perBodyFat:Float

        val db = Firebase.firestore
        val accountDB = db.collection("Account").document(username!!)
        accountDB.get()
            .addOnSuccessListener { document ->
                heightFeet = document.data?.get("Height_Feet") as Double
                heightInch=document.data?.get("Height_Inch") as Double

                if (gender=="Male"){
                    if (flag==1){
                        Lean = 20
                        perBodyFat= 0.18F
                        goalMass(Lean,perBodyFat)
                    }
                    if (flag ==2){
                        Lean =23
                        perBodyFat= 0.15F
                        goalMass(Lean, perBodyFat)
                    }
                    if(flag==3){
                        Lean=26
                        perBodyFat=0.12F
                        goalMass(Lean,perBodyFat)
                    }
                    else if (flag==4){
                        Lean=28
                        perBodyFat=0.10F
                        goalMass(Lean, perBodyFat)
                    }
                }
                else if (gender == "Female"){
                    if (flag==1){
                        Lean = 19
                        perBodyFat= 0.21F
                        goalMass(Lean,perBodyFat)
                    }
                    if (flag ==2){
                        Lean =22
                        perBodyFat= 0.18F
                        goalMass(Lean, perBodyFat)
                    }
                    if(flag==3){
                        Lean=25
                        perBodyFat=0.14F
                        goalMass(Lean,perBodyFat)
                    }
                    else if (flag==4){
                        Lean=27
                        perBodyFat=0.12F
                        goalMass(Lean, perBodyFat)
                    }
                }
            }
    }

    private fun goalMass(Lean:Int, perbodyFat:Float){
//        Toast.makeText(this, username, Toast.LENGTH_SHORT).show()
        val db = Firebase.firestore
        val accountDB = db.collection("Account").document(username!!)

        var goalWeight: Int = ((Lean*(pow(((heightFeet!! *12)+ heightInch!!),2)))/703).toFloat().roundToInt()
        //goalWeight= goalWeight.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toFloat()

        var goalMuscleMass: Int = goalWeight-(goalWeight*perbodyFat).roundToInt()
        //goalMuscleMass= goalMuscleMass.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toFloat()


        val goalData = hashMapOf("Goal_Weight" to goalWeight, "Goal_Muscle_Mass" to goalMuscleMass, "Flag" to flag, "GoalBMI" to Lean)
        accountDB.update(goalData as Map<String, Any>)
    }

    fun pow(n: Double, exp: Int): Float{
        return BigInteger.valueOf(n.toLong()).pow(exp).toFloat()
    }

    fun GetId(v: View) {
        flag = 0
        for (i in 0 until mImgIds!!.size) {
            if (v.getTag() == i) {
                if (gender == "Male"){
                    when (i) {
                        0 ->
                            if (mImgIds!![i] == (R.drawable.male_physique_first_selected)) {
                                maleInitData()
                                flag = 0
                            } else {
                                maleInitData()
                                mImgIds!![i] = R.drawable.male_physique_first_selected
                                flag = 1
                            }

                        1 ->
                            if (mImgIds!![i] == R.drawable.male_physique_second_selected) {
                                maleInitData()
                                flag = 0
                            } else {
                                maleInitData()
                                mImgIds!![i] = R.drawable.male_physique_second_selected
                                flag = 2
                            }

                        2 ->
                            if (mImgIds!![i] == R.drawable.male_physique_third_selected) {
                                maleInitData()
                                flag = 0
                            } else {
                                maleInitData()
                                mImgIds!![i] = R.drawable.male_physique_third_selected
                                flag = 3
                            }
                        3 ->
                            if (mImgIds!![i] == R.drawable.male_physique_fourth_selected) {
                                maleInitData()
                                flag = 0
                            } else {
                                maleInitData()
                                mImgIds!![i] = R.drawable.male_physique_fourth_selected
                                flag = 4
                            }
                    }
                }
                else if (gender=="Female"){
                    when (i) {
                        0 ->
                            if (mImgIds!![i] == (R.drawable.female_physique_first_selected)) {
                                femaleInitData()
                                flag = 0
                            } else {
                                femaleInitData()
                                mImgIds!![i] = R.drawable.female_physique_first_selected
                                flag = 1
                            }

                        1 ->
                            if (mImgIds!![i] == R.drawable.female_physique_second_selected) {
                                femaleInitData()
                                flag = 0
                            } else {
                                femaleInitData()
                                mImgIds!![i] = R.drawable.female_physique_second_selected
                                flag = 2
                            }

                        2 ->
                            if (mImgIds!![i] == R.drawable.female_physique_third_selected) {
                                femaleInitData()
                                flag = 0
                            } else {
                                femaleInitData()
                                mImgIds!![i] = R.drawable.female_physique_third_selected
                                flag = 3
                            }
                        3 ->
                            if (mImgIds!![i] == R.drawable.female_physique_fourth_selected) {
                                femaleInitData()
                                flag = 0
                            } else {
                                femaleInitData()
                                mImgIds!![i] = R.drawable.female_physique_fourth_selected
                                flag = 4
                            }
                    }

                }

            }
        }
        InitView()
    }

    private fun maleInitData() {
        mImgIds = intArrayOf(
            R.drawable.male_physique_first,
            R.drawable.male_physique_second,
            R.drawable.male_physique_third,
            R.drawable.male_physique_fourth
        )
    }
    private fun femaleInitData(){
        mImgIds = intArrayOf(
            R.drawable.female_physique_first,
            R.drawable.female_physique_second,
            R.drawable.female_physique_third,
            R.drawable.female_physique_fourth
        )
    }

    private fun InitView() {
        //mGallery = findViewById<LinearLayout>(R.id.gallery)
        gallery.removeAllViews()
        for (i in 0 until mImgIds!!.size) {
            val view: View = LayoutInflater.from(this).inflate(
                R.layout.layout_listitem,
                gallery, false
            )
            val txttop = view
                .findViewById(R.id.textcardtop) as TextView
            txttop.text = topName!![i]
            val img: ImageView = view
                .findViewById(R.id.imagecard) as ImageView
            img.setImageResource(mImgIds!![i])
            img.setTag(i)
            val txtbottom = view
                .findViewById(R.id.textcardbottom) as TextView
            txtbottom.text = bottomName!![i]
            img.setPadding(20, 10, 20, 10)
            gallery.addView(view)
        }
    }
}