package ar.edu.utn.frba.mobile.a2019c1.superheroes.ui.cards


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.TableRow.LayoutParams
import android.widget.TableRow.LayoutParams.MATCH_PARENT
import android.widget.TableRow.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import ar.edu.utn.frba.mobile.a2019c1.superheroes.R
import ar.edu.utn.frba.mobile.a2019c1.superheroes.adapters.CardsAdapter
import ar.edu.utn.frba.mobile.a2019c1.superheroes.services.ApiService
import ar.edu.utn.frba.mobile.a2019c1.superheroes.services.SessionsService
import ar.edu.utn.frba.mobile.a2019c1.superheroes.ui.registration.RegistrationActivity
import kotlinx.android.synthetic.main.fragment_cards.*


class CardsFragment : Fragment() {

	private val apiService by lazy { ApiService(context!!) }
	private val sessionService by lazy { SessionsService(context!!) }
	private var cardsSelectedCounter = 0

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
		inflater.inflate(R.layout.fragment_cards, container, false)

	override fun onResume() {
		super.onResume()
		val gvCards = view!!.findViewById<GridView>(R.id.cards_grid_view)
		getUserAvailableCards(gvCards)
		getUserTeam()
		onCardClick(gvCards)
		onCreateTeamButtonClick()
	}

	private fun getUserAvailableCards(gvCards: GridView) {
		val spinner = cards_spinner.apply { visibility = View.VISIBLE }
		sessionService.getLoggedUser()?.let { loggedUser ->
			apiService.getUserAvailableCards(loggedUser, { cards ->
				val cardsAdapter = CardsAdapter(cards, context!!)
				gvCards.adapter = cardsAdapter
				spinner.visibility = GONE
			}, { error ->
				println("Failed to get user cards - error: $error")
				spinner.visibility = INVISIBLE
			})
		} ?: handleUserNotLogged()
	}

	private fun getUserTeam() {
		val table = view!!.findViewById<TableLayout>(R.id.table_user_team)
		val row = TableRow(context)
		val rowLayoutParams = LayoutParams(MATCH_PARENT, 0, 1f)
		row.layoutParams = rowLayoutParams
		for (i in 0..3) {
			val ivSuperhero = ImageView(context)
			ivSuperhero.layoutParams = LayoutParams(0, WRAP_CONTENT, 0.25f)
			ivSuperhero.setImageResource(R.drawable.ic_launcher_foreground)
			row.addView(ivSuperhero)
		}
		table.addView(row, 0)
	}

	private fun onCardClick(gvCards: GridView) {
		gvCards.onItemClickListener = OnItemClickListener { parent, _view, position, id ->
			cardsSelectedCounter++.takeIf { it <= 3 }?.let {
				val btnCreateTeam = view!!.findViewById<Button>(R.id.btn_create_team)
				btnCreateTeam.text = getString(R.string.btn_create_team_counter, cardsSelectedCounter)
			}
		}
	}

	private fun onCreateTeamButtonClick() {
		view!!.findViewById<Button>(R.id.btn_create_team).let { button ->
			button.setOnClickListener {
				cardsSelectedCounter = 0
				button.text = getString(R.string.btn_create_team)
			}
		}
	}

	private fun handleUserNotLogged() {
		println("Failed to get logged user in cards fragment")
		val intent = Intent(activity, RegistrationActivity::class.java)
		startActivity(intent)
		activity!!.setResult(Activity.RESULT_OK)
		activity!!.finish()
	}

	companion object {
		@JvmStatic
		fun newInstance() = CardsFragment()
	}

}
