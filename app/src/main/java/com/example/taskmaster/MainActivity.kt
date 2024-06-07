package com.example.taskmaster

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.taskmaster.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), TaskItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private  lateinit var mainLayout: ConstraintLayout

    //logo
    private lateinit var logoLayout: LinearLayout
    private lateinit var logoImage: ImageView
    private lateinit var logoTitle: TextView

    //login
    private lateinit var loginTitle: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var username: SharedPreferences
    private lateinit var enterButton: Button
    private lateinit var loginLayout: LinearLayout
    private lateinit var bgBlob: ImageView

    //tasks
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel

    private lateinit var newTaskButton: Button

    //navigation
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var frameContainer: FrameLayout
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainLayout = findViewById(R.id.main)

        //logo
        logoLayout = findViewById(R.id.logoLayout)
        logoImage = findViewById(R.id.logoImage)
        logoTitle = findViewById(R.id.logoTitle)

        //login
        loginTitle = findViewById(R.id.loginTitle)
        loginLayout = findViewById(R.id.loginLayout)
        bgBlob = findViewById(R.id.bgBlob)

        //username
        username = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        usernameEditText = findViewById(R.id.usernameInput)

        val storedUsername = username.getString("username", "")

        enterButton = findViewById(R.id.enterButton)
        enterButton.setOnClickListener {
            saveUsername()
        }

        //tasks
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }

        newTaskButton = findViewById(R.id.newTaskButton)

        //navigation
        frameContainer = findViewById(R.id.fragmentContainer)
        drawerLayout = findViewById(R.id.drawerLayout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All"

        navigationView = findViewById(R.id.navView)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = getColor(R.color.blue)

        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(AllFragment(), "All")
            navigationView.setCheckedItem(R.id.all)
        }

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        //ANIMATIONS

        //LOGO ANIMATIONS

        //logo image
        val slideInLogoImg = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, -5f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
        )
        slideInLogoImg.duration = 1500L
        slideInLogoImg.fillAfter = true
        slideInLogoImg.interpolator = AccelerateDecelerateInterpolator()

        slideInLogoImg.startOffset = 200L

        logoImage.startAnimation(slideInLogoImg)

        //logo title
        val slideInLogoTitle = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, -8f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
        )
        slideInLogoTitle.duration = 1500L
        slideInLogoTitle.fillAfter = true
        slideInLogoTitle.interpolator = AccelerateDecelerateInterpolator()

        logoTitle.startAnimation(slideInLogoTitle)

        //LOGIN ANIMATIONS

        binding.loginTitle.visibility = View.GONE
        binding.usernameInput.visibility = View.GONE
        binding.enterButton.visibility = View.GONE

        binding.infoButton.visibility = View.GONE

        //bg blob
        val slideInBlob = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 1.5f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
        )
        slideInBlob.duration = 1500L
        slideInBlob.fillAfter = true
        slideInBlob.interpolator = AccelerateDecelerateInterpolator()

        slideInBlob.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                binding.loginTitle.visibility = View.VISIBLE
                binding.usernameInput.visibility = View.VISIBLE
                binding.enterButton.visibility = View.VISIBLE

                binding.infoButton.visibility = View.VISIBLE

                //info button
                val slideInInfoButton = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, // Start X position
                    Animation.RELATIVE_TO_SELF, 0f, // End X position
                    Animation.RELATIVE_TO_SELF, -1f, // Start Y position (above the screen)
                    Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                )
                slideInInfoButton.duration = 800L
                slideInInfoButton.fillAfter = true
                slideInInfoButton.interpolator = AccelerateDecelerateInterpolator()

                binding.infoButton.startAnimation(slideInInfoButton)

                //login title
                val slideInLogInTitle = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, // Start X position
                    Animation.RELATIVE_TO_SELF, 0f, // End X position
                    Animation.RELATIVE_TO_SELF, 8f, // Start Y position (above the screen)
                    Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                )
                slideInLogInTitle.duration = 1500L
                slideInLogInTitle.fillAfter = true
                slideInLogInTitle.interpolator = AccelerateDecelerateInterpolator()

                binding.loginTitle.startAnimation(slideInLogInTitle)

                //username input
                val slideInName = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, // Start X position
                    Animation.RELATIVE_TO_SELF, 0f, // End X position
                    Animation.RELATIVE_TO_SELF, 4f, // Start Y position (above the screen)
                    Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                )
                slideInName.duration = 1500L
                slideInName.fillAfter = true
                slideInName.interpolator = AccelerateDecelerateInterpolator()

                slideInName.startOffset = 400L

                binding.usernameInput.startAnimation(slideInName)

                //enter button
                val slideInEnterButton = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, // Start X position
                    Animation.RELATIVE_TO_SELF, 0f, // End X position
                    Animation.RELATIVE_TO_SELF, 3f, // Start Y position (above the screen)
                    Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                )
                slideInEnterButton.duration = 1500L
                slideInEnterButton.fillAfter = true
                slideInEnterButton.interpolator = AccelerateDecelerateInterpolator()

                slideInEnterButton.startOffset = 800L

                binding.enterButton.startAnimation(slideInEnterButton)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        bgBlob.startAnimation(slideInBlob)

        //logo and login exit animations
        val loggedIn = username.getBoolean("loggedIn", false)
        if (loggedIn) {
            val headerView = navigationView.getHeaderView(0)
            val usernameTextView = headerView.findViewById<TextView>(R.id.usernameTextView)
            val storedUsername = username.getString("username", "")

            usernameTextView.text = "Hello, $storedUsername!"

            logoLayout.visibility = View.GONE
            loginLayout.visibility = View.GONE

            bgBlob.visibility = View.GONE
            mainLayout.setBackgroundColor(resources.getColor(R.color.background))

            newTaskButton.visibility = View.VISIBLE
            frameContainer.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
        } else {
            logoLayout.visibility = View.VISIBLE
            loginLayout.visibility = View.VISIBLE
            bgBlob.visibility = View.VISIBLE

            newTaskButton.visibility = View.GONE
            frameContainer.visibility = View.GONE
            toolbar.visibility = View.GONE
        }

        binding.infoButton.setOnClickListener() {
            showPopup()
        }
    }

    private fun saveUsername() {
        val usernameText = usernameEditText.text.toString()

        if (usernameText.isNotEmpty()) {
            val editor = username.edit()
            editor.putString("username", usernameText)
            editor.putBoolean("loggedIn", true)
            editor.apply()

            handlePostLoginAnimations()
        } else {
            usernameEditText.error = "Username cannot be empty"
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePostLoginAnimations() {
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.usernameTextView)
        val storedUsername = username.getString("username", "")

        usernameTextView.text = "Hello, $storedUsername!"

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 500L
        fadeOut.fillAfter = true

        //info button
        val slidOutInfoButton = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f
        )
        slidOutInfoButton.duration = 800L
        slidOutInfoButton.fillAfter = true
        slidOutInfoButton.interpolator = AccelerateDecelerateInterpolator()

        val infoButtonAnimations = AnimationSet(true)
        infoButtonAnimations.addAnimation(slidOutInfoButton)
        infoButtonAnimations.addAnimation(fadeOut)

        binding.infoButton.startAnimation(infoButtonAnimations)

        //logo image
        val slideOutLogoImg = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 0f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, -5f // End Y position (original position)
        )
        slideOutLogoImg.duration = 1500L
        slideOutLogoImg.fillAfter = true
        slideOutLogoImg.interpolator = AccelerateDecelerateInterpolator()

        val logoImageAnimation = AnimationSet(true)
        logoImageAnimation.addAnimation(slideOutLogoImg)
        logoImageAnimation.addAnimation(fadeOut)

        logoImage.startAnimation(logoImageAnimation)

        //logo title
        val slideOutLogoTitle = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 0f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, -8f // End Y position (original position)
        )
        slideOutLogoTitle.duration = 1500L
        slideOutLogoTitle.fillAfter = true
        slideOutLogoTitle.interpolator = AccelerateDecelerateInterpolator()

        val logoTitleAnimation = AnimationSet(true)
        logoTitleAnimation.addAnimation(slideOutLogoTitle)
        logoTitleAnimation.addAnimation(fadeOut)

        logoTitleAnimation.startOffset = 400L

        logoTitle.startAnimation(logoTitleAnimation)

        //login title
        val slideOutLogInTitle = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 0f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 8f // End Y position (original position)
        )
        slideOutLogInTitle.duration = 1500L
        slideOutLogInTitle.fillAfter = true
        slideOutLogInTitle.interpolator = AccelerateDecelerateInterpolator()

        val logInTitleAnimation = AnimationSet(true)
        logInTitleAnimation.addAnimation(slideOutLogInTitle)
        logInTitleAnimation.addAnimation(fadeOut)

        logInTitleAnimation.startOffset = 800L

        binding.loginTitle.startAnimation(logInTitleAnimation)

        //username input
        val slideInName = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 0f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 4f // End Y position (original position)
        )
        slideInName.duration = 1500L
        slideInName.fillAfter = true
        slideInName.interpolator = AccelerateDecelerateInterpolator()

        val usernameAnimation = AnimationSet(true)
        usernameAnimation.addAnimation(slideInName)
        usernameAnimation.addAnimation(fadeOut)

        usernameAnimation.startOffset = 400L

        binding.usernameInput.startAnimation(usernameAnimation)

        //enter button
        val slideOutEnterButton = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, // Start X position
            Animation.RELATIVE_TO_SELF, 0f, // End X position
            Animation.RELATIVE_TO_SELF, 0f, // Start Y position (above the screen)
            Animation.RELATIVE_TO_SELF, 3f // End Y position (original position)
        )
        slideOutEnterButton.duration = 1500L
        slideOutEnterButton.fillAfter = true
        slideOutEnterButton.interpolator = AccelerateDecelerateInterpolator()

        val enterButtonAnimation = AnimationSet(true)
        enterButtonAnimation.addAnimation(slideOutEnterButton)
        enterButtonAnimation.addAnimation(fadeOut)

        binding.enterButton.startAnimation(enterButtonAnimation)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                logoLayout.visibility = View.GONE
                loginLayout.visibility = View.GONE
                binding.infoButton.visibility = View.GONE

                val blobAnimator = ObjectAnimator.ofFloat(bgBlob, "scaleY", 1f, 5f)

                blobAnimator.duration = 1200
                blobAnimator.interpolator = AccelerateDecelerateInterpolator()

                blobAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        bgBlob.visibility = View.GONE
                        mainLayout.setBackgroundColor(resources.getColor(R.color.background))

                        newTaskButton.visibility = View.VISIBLE
                        frameContainer.visibility = View.VISIBLE
                        toolbar.visibility = View.VISIBLE

                        //ANIMATIONS

                        //new task button
                        val slideInNewTaskButton = TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, // Start X position
                            Animation.RELATIVE_TO_SELF, 0f, // End X position
                            Animation.RELATIVE_TO_SELF, 1f, // Start Y position (above the screen)
                            Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                        )
                        slideInNewTaskButton.duration = 500L
                        slideInNewTaskButton.fillAfter = true
                        slideInNewTaskButton.interpolator = OvershootInterpolator()

                       newTaskButton.startAnimation(slideInNewTaskButton)

                        //toolbar
                        val slideInToolbar = TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, // Start X position
                            Animation.RELATIVE_TO_SELF, 0f, // End X position
                            Animation.RELATIVE_TO_SELF, -1f, // Start Y position (above the screen)
                            Animation.RELATIVE_TO_SELF, 0f // End Y position (original position)
                        )
                        slideInToolbar.duration = 500L
                        slideInToolbar.fillAfter = true
                        slideInToolbar.interpolator = AccelerateDecelerateInterpolator()

                        toolbar.startAnimation(slideInToolbar)

                        //frame container
                        val zoomInFrameContainer = ScaleAnimation(
                            0.0f, 1.0f,
                            0.0f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f
                        )
                        zoomInFrameContainer.duration = 500L
                        zoomInFrameContainer.fillAfter = true
                        zoomInFrameContainer.interpolator = OvershootInterpolator()
                        zoomInFrameContainer.startOffset = 400L

                        frameContainer.startAnimation(zoomInFrameContainer)

                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })

                blobAnimator.start()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager,"newTaskTag" )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
    }

    override fun importantTaskItem(taskItem: TaskItem) {
        taskViewModel.setImportant(taskItem)
    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all -> replaceFragment(AllFragment(), "All")

            R.id.today -> replaceFragment(TodayFragment(), "Today")


            R.id.important -> replaceFragment(ImportantFragment(), "Important")

            R.id.scheduled -> replaceFragment(ScheduledFragment(), "Scheduled")

            R.id.chores -> replaceFragment(ChoresFragment(), "Chores")


            R.id.work -> replaceFragment(WorkFragment(), "Work")


            R.id.school -> replaceFragment(SchoolFragment(), "School")


            R.id.other -> replaceFragment(OtherFragment(), "Others")


            R.id.completed -> replaceFragment(CompletedFragment(), "Completed")



        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.activity_about_popup, null)

        val width = FrameLayout.LayoutParams.WRAP_CONTENT
        val height = 1250

        val instructWindow = PopupWindow(popupView, width, height, true)
        instructWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val zoomInAnimation = ScaleAnimation(
            0.2f, 1.0f,
            0.2f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        zoomInAnimation.duration = 500
        zoomInAnimation.fillAfter = true

        zoomInAnimation.interpolator = AccelerateDecelerateInterpolator()

        popupView.startAnimation(zoomInAnimation)

        instructWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val closeButton = popupView.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            val zoomOutAnimation = ScaleAnimation(
                1.0f, 0.0f,
                1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            zoomOutAnimation.duration = 500
            zoomOutAnimation.fillAfter = true
            zoomOutAnimation.interpolator = AccelerateDecelerateInterpolator()

            popupView.startAnimation(zoomOutAnimation)

            zoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    instructWindow.dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }

}